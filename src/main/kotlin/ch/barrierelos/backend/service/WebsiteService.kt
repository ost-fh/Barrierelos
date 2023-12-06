package ch.barrierelos.backend.service

import ch.barrierelos.backend.constants.Queueing
import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toMessage
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.converter.scanner.toScanJob
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebsiteEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.InvalidDomainException
import ch.barrierelos.backend.exceptions.InvalidUrlException
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.WebsiteResultMessage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.Repository.Companion.throwIfNotExists
import ch.barrierelos.backend.repository.WebsiteRepository
import ch.barrierelos.backend.repository.WebsiteTagRepository
import ch.barrierelos.backend.repository.scanner.ScanJobRepository
import ch.barrierelos.backend.repository.scanner.WebpageResultRepository
import ch.barrierelos.backend.repository.scanner.WebsiteResultRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.*
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebsiteService
{
  private val logger: Logger = LoggerFactory.getLogger(WebsiteService::class.java)

  @Autowired
  private lateinit var websiteRepository: WebsiteRepository

  @Autowired
  private lateinit var websiteTagRepository: WebsiteTagRepository

  @Autowired
  private lateinit var scanJobRepository: ScanJobRepository

  @Autowired
  private lateinit var websiteResultRepository: WebsiteResultRepository

  @Autowired
  private lateinit var webpageResultRepository: WebpageResultRepository

  @Autowired
  private lateinit var queue: RabbitTemplate

  @Autowired
  private lateinit var scoringService: ScoringService

  public fun addWebsite(website: Website): Website
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    if(!Security.hasRole(RoleEnum.ADMIN))
    {
      Security.assertId(website.userId)

      throwIfNewTagWithDifferentUser(website)
    }

    throwIfNoValidDomain(website)
    throwIfUrlNotMatchesDomain(website)
    throwIfDuplicateTags(website)
    throwIfDomainAlreadyExists(website)

    val timestamp = System.currentTimeMillis()
    website.created = timestamp
    website.modified = timestamp
    website.status = StatusEnum.PENDING_INITIAL

    val tags = website.tags.toSet()
    this.websiteRepository.save(website.apply { this.tags.clear() }.toEntity()).toModel(website)

    website.tags.addAll(tags)
    website.tags.forEach { it.websiteId = website.id }

    return this.websiteRepository.save(website.toEntity()).toModel()
  }

  public fun scanWebsite(website: WebsiteMessage)
  {
    if(website.website.endsWith("/"))
      throw IllegalArgumentException("websiteBaseUrl must not end with a slash")
    if(website.webpages.any { !it.startsWith("/") })
      throw IllegalArgumentException("webpagePaths must start with a slash")

    val scanJobEntity = website.toScanJob().toEntity()
    val scanJobMessage = this.scanJobRepository.save(scanJobEntity).toModel().toMessage()

    this.queue.send(Queueing.QUEUE_SCAN_JOB, scanJobMessage.toJson())
  }

  @RabbitListener(queues = [Queueing.QUEUE_SCAN_RESULT])
  @Transactional
  public fun receiveResult(message: String)
  {
    val websiteResultMessage = message.fromJson<WebsiteResultMessage>()

    if(websiteResultMessage.scanStatus == ScanStatusEnum.FAILED)
    {
      logger.error("Scan failed with error: ${websiteResultMessage.errorMessage}, message: $message")
      return
    }

    val scanJobEntity = this.scanJobRepository.findById(websiteResultMessage.jobId).orThrow(NoSuchElementException())
    val websiteResultEntity = this.websiteResultRepository.save(websiteResultMessage.toEntity(scanJobEntity))
    webpageResultRepository.saveAll(websiteResultMessage.webpages.map { it.toEntity(websiteResultEntity) })

    this.scoringService.onReceiveResult(websiteResultEntity.toModel())
  }

  public fun updateWebsite(website: Website): Website
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val existingWebsite = this.websiteRepository.findById(website.id).orElseThrow().toModel()

    val timestamp = System.currentTimeMillis()

    for(tag in website.tags)
    {
      if(tag.websiteId == 0L)
      {
        tag.websiteId = website.id
        tag.modified = timestamp
        tag.created = timestamp
      }
    }

    website.tags.forEach { if(it.websiteId == 0L) it.websiteId = website.id }

    if(Security.hasRole(RoleEnum.MODERATOR))
    {
      throwIfIllegallyModified(website, existingWebsite)
      throwIfTagsIllegallyModified(website, existingWebsite)
    }
    else if(!Security.hasRole(RoleEnum.ADMIN))
    {
      throwIfStatusChanged(website, existingWebsite)
      throwIfIllegallyModified(website, existingWebsite)
      throwIfTagsIllegallyModified(website, existingWebsite)
    }

    throwIfDuplicateTags(website)

    website.modified = timestamp

    return this.websiteRepository.save(website.toEntity()).toModel()
  }

  public fun getWebsites(defaultParameters: DefaultParameters = DefaultParameters()): Result<Website>
  {
    return this.websiteRepository.findAll(defaultParameters, WebsiteEntity::class.java, WebsiteEntity::toModel)
  }

  public fun getWebsite(websiteId: Long): Website
  {
    return this.websiteRepository.findById(websiteId).orElseThrow().toModel()
  }

  public fun deleteWebsite(websiteId: Long)
  {
    val existingWebsite = this.websiteRepository.findById(websiteId).orElseThrow().toModel()

    Security.assertAnyRolesOrId(existingWebsite.userId, RoleEnum.ADMIN, RoleEnum.MODERATOR)

    this.websiteTagRepository.deleteAllByWebsiteFk(websiteId)

    this.websiteRepository.deleteById(websiteId)
  }

  private fun throwIfTagsIllegallyModified(website: Website, existingWebsite: Website)
  {
    for(tag in website.tags)
    {
      if(tag.id == 0L)
      {
        if(existingWebsite.tags.any { it.tag.id == tag.tag.id })
        {
          throw AlreadyExistsException("Tag for that website already exists.")
        }
      }
      else
      {
        val existingTag = existingWebsite.tags.find { it.id == tag.id }

        if(existingTag == null)
        {
          throw NoSuchElementException("Tag for that website does not exist.")
        }
        else if(tag != existingTag)
        {
          throw IllegalArgumentException("Tag cannot be modified.")
        }
      }
    }
  }

  private fun throwIfIllegallyModified(website: Website, existingWebsite: Website)
  {
    if((website.userId != existingWebsite.userId)
      || (website.domain != existingWebsite.domain)
      || (website.url != existingWebsite.url)
      || (website.status != existingWebsite.status)
      || (website.created != existingWebsite.created)
      || (website.status == StatusEnum.PENDING_INITIAL)
      || (website.status == StatusEnum.PENDING_RESCAN)
      || (existingWebsite.status == StatusEnum.PENDING_INITIAL)
      || (existingWebsite.status == StatusEnum.PENDING_RESCAN))
    {
      throw IllegalArgumentException("Website illegally modified.")
    }
  }

  private fun throwIfStatusChanged(website: Website, existingWebsite: Website)
  {
    if(website.status != existingWebsite.status)
    {
      throw IllegalArgumentException("Website illegally modified.")
    }
  }

  private fun throwIfNewTagWithDifferentUser(website: Website)
  {
    for(websiteTag in website.tags)
    {
      if(websiteTag.id == 0L)
      {
        Security.assertId(websiteTag.userId)
      }
      else
      {
        this.websiteTagRepository.throwIfNotExists(websiteTag.id)
      }
    }
  }

  private fun throwIfDomainAlreadyExists(website: Website)
  {
    if(this.websiteRepository.existsByDomain(website.domain))
    {
      throw AlreadyExistsException("Website with that domain already exists.")
    }
  }

  private fun throwIfDuplicateTags(website: Website)
  {
    if(website.tags.containsDuplicates { tag -> tag.tag.id })
    {
      throw AlreadyExistsException("Duplicate tags found.")
    }
  }

  private fun throwIfNoValidDomain(website: Website)
  {
    if(!website.domain.matches("^(((?!-))(xn--|_)?[a-z0-9-]{0,61}[a-z0-9]{1,1}\\.)*(xn--)?([a-z0-9][a-z0-9\\-]{0,60}|[a-z0-9-]{1,30}\\.[a-z]{2,})\$".toRegex()))
    {
      throw InvalidDomainException("Domain is not valid.")
    }
  }

  private fun throwIfUrlNotMatchesDomain(website: Website)
  {
    if(website.url != "http://${website.domain}" && website.url != "https://${website.domain}")
    {
      throw InvalidUrlException("Domain and url do not match.")
    }
  }
}
