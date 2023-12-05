package ch.barrierelos.backend.service

import ch.barrierelos.backend.constants.Queueing
import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toMessage
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.converter.scanner.toScanJobEntity
import ch.barrierelos.backend.entity.scanner.ScanJobEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.WebsiteResultMessage
import ch.barrierelos.backend.model.scanner.ScanJob
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.scanner.ScanJobRepository
import ch.barrierelos.backend.repository.scanner.WebsiteResultRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.fromJson
import ch.barrierelos.backend.util.send
import ch.barrierelos.backend.util.toJson
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrDefault

@Service
public class WebsiteService(private val queue: RabbitTemplate)
{
  private val logger: Logger = LoggerFactory.getLogger(WebsiteService::class.java)

  @Autowired
  private lateinit var scoringService: ScoringService

  @Autowired
  private lateinit var scanJobRepository: ScanJobRepository

  @Autowired
  private lateinit var websiteResultRepository: WebsiteResultRepository

  public fun scanWebsite(website: WebsiteMessage)
  {
    if(website.website.endsWith("/"))
      throw IllegalArgumentException("websiteBaseUrl must not end with a slash")
    if(website.webpages.any { !it.startsWith("/") })
      throw IllegalArgumentException("webpagePaths must start with a slash")

    val scanJobEntity = website.toScanJobEntity()
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

    val scanJobEntity = this.scanJobRepository.findById(websiteResultMessage.jobId).getOrDefault(null)
    if(scanJobEntity != null)
    {
      websiteResultRepository.deleteByScanJobFk(scanJobEntity.scanJobId)
    }
    val websiteResultEntity = this.websiteResultRepository.save(websiteResultMessage.toEntity(scanJobEntity))

    this.scoringService.onReceiveResult(websiteResultEntity.toModel())
  }

  public fun getScanJobs(defaultParameters: DefaultParameters = DefaultParameters()): Result<ScanJob>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    return this.scanJobRepository.findAll(
      defaultParameters,
      ScanJobEntity::class.java,
      ScanJobEntity::toModel
    )
  }

  public fun getScanJob(jobId: Long): ScanJob
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    return this.scanJobRepository.findById(jobId).orElseThrow().toModel()
  }

  public fun deleteScanJob(jobId: Long)
  {
    Security.assertRole(RoleEnum.ADMIN)

    this.scanJobRepository.checkIfExists(jobId)

    this.scanJobRepository.deleteById(jobId)
  }
}
