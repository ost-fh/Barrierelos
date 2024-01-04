package ch.barrierelos.backend.service

import ch.barrierelos.backend.constant.Queueing
import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toMessage
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.converter.scanner.toScanJob
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import ch.barrierelos.backend.exception.*
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.WebsiteResultMessage
import ch.barrierelos.backend.model.*
import ch.barrierelos.backend.model.scanner.ScanJob
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.repository.TagRepository
import ch.barrierelos.backend.repository.WebpageRepository
import ch.barrierelos.backend.repository.WebsiteRepository
import ch.barrierelos.backend.repository.WebsiteRepository.Companion.findAll
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
  private lateinit var webpageRepository: WebpageRepository

  @Autowired
  private lateinit var tagRepository: TagRepository

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
  private lateinit var statisticService: StatisticService


  @Transactional
  public fun addWebsite(websiteMessage: WebsiteMessage): Website
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    throwIfNoValidUrl(websiteMessage.url)
    val domain = "^https?://(([^/?]+\\.)+[^/?]+)(/.*)?$".toRegex()
      .find(websiteMessage.url)?.groups?.get(1)?.value
      ?: throw InvalidUrlException(websiteMessage.url)
    throwIfDomainAlreadyExists(domain)

    val tags = tagRepository.findAllByNameIn(websiteMessage.tags).toModels()
    if(tags.size < websiteMessage.tags.size)
    {
      throw NoSuchElementException("Tag with that name does not exist.")
    }
    validateTags(tags, websiteMessage.category)

    var website = websiteMessage.toModel(domain, mutableSetOf())
    website = websiteRepository.save(website.toEntity()).toModel()

    val websiteTags = tags.map {
      WebsiteTag(
        websiteId = website.id,
        userId = Security.getUserId(),
        tag = it,
      )
    }.toMutableSet()
    website.tags = websiteTagRepository.saveAll(websiteTags.map { it.toEntity() }).toModels()

    statisticService.addWebsiteScan(website)

    return website
  }

  private fun validateTags(tags: MutableSet<Tag>, category: CategoryEnum)
  {
    val countries = tags.filter { it.name.startsWith("Country:") }
    val cantons = tags.filter { it.name.startsWith("Canton:") }

    if(countries.size > 1)
    {
      throw InvalidArgumentException("Only one country tag per website is allowed.")
    }
    else if(countries.size == 1)
    {
      if(cantons.isNotEmpty())
        throw InvalidArgumentException("Country and canton tag cannot be used together.")
    }

    if(cantons.size > 1)
    {
      throw InvalidArgumentException("Only one canton tag per website is allowed.")
    }
    else if(cantons.size == 1)
    {
      if(category == CategoryEnum.GOVERNMENT_FEDERAL)
      {
        throw InvalidArgumentException("Federal government websites cannot have a canton tag.")
      }
    } else {
      if(mutableSetOf(CategoryEnum.GOVERNMENT_CANTONAL, CategoryEnum.GOVERNMENT_MUNICIPAL).contains(category))
      {
        throw InvalidArgumentException("Cantonal and municipal websites must have a canton tag.")
      }
    }
  }

  @RabbitListener(queues = [Queueing.QUEUE_SCAN_RESULT])
  public fun receiveResult(message: String)
  {
    val websiteResultMessage = message.fromJson<WebsiteResultMessage>()

    if(websiteResultMessage.scanStatus == ScanStatusEnum.FAILED)
    {
      logger.error("Scan failed with error: ${websiteResultMessage.errorMessage}, message: $message")
      return
    }

    val scanJobEntity = this.scanJobRepository.findById(websiteResultMessage.jobId).orElseThrow()
    val websiteResult = this.websiteResultRepository.save(websiteResultMessage.toEntity(scanJobEntity)).toModel()
    val webpageResults = this.webpageResultRepository.saveAll(websiteResultMessage.webpages.map { it.toEntity(websiteResult.toEntity()) })
      .map { it.toModel() }.toMutableSet()

    this.statisticService.onReceiveResult(websiteResult, webpageResults)
  }

  @Transactional
  public fun scanWebsite(website: Website, webpages: MutableSet<Webpage>? = null): ScanJob
  {
    throwIfDeleted(website)

    val scanWebpages = webpages ?: webpageRepository.findAllByWebsiteWebsiteId(website.id).map { it.toModel() }.toMutableSet()
    if(scanWebpages.isEmpty()) throw InvalidStateException("Website has no webpages.")

    val scanJob = website.toScanJob(scanWebpages)
    val scanJobMessage = this.scanJobRepository.save(scanJob.toEntity()).toModel().toMessage()

    this.queue.send(Queueing.QUEUE_SCAN_JOB, scanJobMessage.toJson())
    return scanJob
  }

  @Transactional
  public fun scanWebsite(id: Long): ScanJob
  {
    return scanWebsite(this.websiteRepository.findById(id).orElseThrow().toModel())
  }

  public fun searchWebsiteByDomain(domain: String): Set<Website>
  {
    return this.websiteRepository.findByDomainContainingOrderByDomain(domain).map { it.toModel() }.toSet()
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
    else if(Security.hasRole(RoleEnum.CONTRIBUTOR))
    {
      if(!Security.hasId(website.user.id))
      {
        throwIfDeleted(website)
      }

      throwIfStatusIllegallyChanged(website, existingWebsite)
      throwIfIllegallyModified(website, existingWebsite)
      throwIfTagsIllegallyModified(website, existingWebsite)
    }

    website.modified = timestamp

    return this.websiteRepository.save(website.toEntity()).toModel()
  }

  public fun getWebsites(showDeleted: Boolean = false, showBlocked: Boolean = false, defaultParameters: DefaultParameters = DefaultParameters()): Result<Website>
  {
    return this.websiteRepository.findAll(showDeleted, showBlocked, defaultParameters)
  }

  public fun getWebsite(websiteId: Long): Website
  {
    return this.websiteRepository.findById(websiteId).orElseThrow().toModel()
  }

  public fun getRegions(defaultParameters: DefaultParameters): Result<Region>
  {
    defaultParameters.apply {
      sort = null
      order = null
    }
    val regionPage = this.websiteRepository.findAllRegions(defaultParameters.toPageable(Region::class.java))
    return Result(regionPage.content, regionPage.totalElements)
  }

  @Transactional
  public fun deleteWebsite(websiteId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    throwIfNotExists(websiteId)

    this.websiteTagRepository.deleteAllByWebsiteFk(websiteId)

    this.websiteRepository.deleteById(websiteId)
  }

  private fun throwIfDeleted(website: Website)
  {
    if(website.deleted)
    {
      throw NoAuthorizationException()
    }
  }

  private fun throwIfTagsIllegallyModified(website: Website, existingWebsite: Website)
  {
    for(tag in website.tags)
    {
      if(tag.id == 0L)
      {
        if(existingWebsite.tags.any { it.tag.id == tag.tag.id })
        {
          throw ReferenceNotExistsException("This tag already exists for this website.")
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
    if((website.user != existingWebsite.user)
      || (website.domain != existingWebsite.domain)
      || (website.url != existingWebsite.url)
      || (website.created != existingWebsite.created)
      || (website.status != existingWebsite.status && (website.status == StatusEnum.PENDING_INITIAL || website.status == StatusEnum.PENDING_RESCAN || website.status == StatusEnum.READY))
    )
    {
      throw IllegalArgumentException("Website illegally modified.")
    }
  }

  private fun throwIfStatusIllegallyChanged(website: Website, existingWebsite: Website)
  {
    if(website.status != existingWebsite.status && website.status == StatusEnum.BLOCKED)
    {
      throw IllegalArgumentException("Website illegally modified.")
    }
  }

  private fun throwIfDomainAlreadyExists(domain: String)
  {
    if(this.websiteRepository.existsByDomain(domain))
    {
      throw AlreadyExistsException("Website with this domain already exists.")
    }
  }

  private fun throwIfNotExists(websiteId: Long)
  {
    if(!this.websiteRepository.existsById(websiteId))
    {
      throw NoSuchElementException("Website with this id does not exist.")
    }
  }
}
