package ch.barrierelos.backend.service

import ch.barrierelos.backend.constants.Queueing
import ch.barrierelos.backend.converter.toAnalysisJobMessage
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.scanner.AnalysisJobEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.AnalysisResultMessage
import ch.barrierelos.backend.model.scanner.AnalysisJob
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.scanner.AnalysisJobRepository
import ch.barrierelos.backend.repository.scanner.AnalysisResultRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.fromJson
import ch.barrierelos.backend.util.send
import ch.barrierelos.backend.util.toJson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class AnalysisService(private val queue: RabbitTemplate)
{
  @Autowired
  private lateinit var scoringService: ScoringService
  @Autowired
  private lateinit var analysisJobRepository: AnalysisJobRepository
  @Autowired
  private lateinit var analysisResultRepository: AnalysisResultRepository

  public fun scanWebsite(website: WebsiteMessage)
  {
    if (website.website.endsWith("/"))
      throw IllegalArgumentException("websiteBaseUrl must not end with a slash")
    if (website.webpages.any { !it.startsWith("/") })
      throw IllegalArgumentException("webpagePaths must start with a slash")

    val analysisJobMessage = website.toAnalysisJobMessage()
    this.analysisJobRepository.save(analysisJobMessage.toEntity())

    this.queue.send(Queueing.QUEUE_JOB, analysisJobMessage.toJson())
  }

  @RabbitListener(queues = [Queueing.QUEUE_RESULT])
  private fun receiveResult(message: String)
  {
    val analysisResultMessage = message.fromJson<AnalysisResultMessage>()
    val analysisResult = this.analysisResultRepository.save(analysisResultMessage.toEntity())

    this.scoringService.onReceiveResult(analysisResult.toModel())
  }

  public fun getJobs(defaultParameters: DefaultParameters = DefaultParameters()): Result<AnalysisJob>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    return this.analysisJobRepository.findAll(defaultParameters, AnalysisJobEntity::class.java, AnalysisJobEntity::toModel)
  }

  public fun getJob(jobId: Long): AnalysisJob
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    return this.analysisJobRepository.findById(jobId).orElseThrow().toModel()
  }

  public fun deleteJob(jobId: Long)
  {
    Security.assertRole(RoleEnum.ADMIN)

    this.analysisJobRepository.checkIfExists(jobId)

    this.analysisJobRepository.deleteById(jobId)
  }
}
