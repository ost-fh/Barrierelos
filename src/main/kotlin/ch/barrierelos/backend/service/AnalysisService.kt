package ch.barrierelos.backend.service

import ch.barrierelos.backend.constants.Queueing
import ch.barrierelos.backend.converter.toAnalysisJobMessage
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.scanner.AnalysisJobEntity
import ch.barrierelos.backend.entity.scanner.AnalysisResultEntity
import ch.barrierelos.backend.entity.scanner.CheckElementEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.AnalysisResultMessage
import ch.barrierelos.backend.message.scanner.CheckElementMessage
import ch.barrierelos.backend.model.scanner.AnalysisJob
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.scanner.*
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
  private lateinit var analysisJobRepository: AnalysisJobRepository
  @Autowired
  private lateinit var analysisResultRepository: AnalysisResultRepository
  @Autowired
  private lateinit var webpageResultRepository: WebpageResultRepository
  @Autowired
  private lateinit var ruleRepository: RuleRepository
  @Autowired
  private lateinit var checkRepository: CheckRepository
  @Autowired
  private lateinit var elementRepository: ElementRepository

  public fun scanWebsite(website: WebsiteMessage)
  {
    val analysisJobMessage = website.toAnalysisJobMessage()

    this.analysisJobRepository.save(analysisJobMessage.toEntity())

    this.queue.send(Queueing.QUEUE_JOB, analysisJobMessage.toJson())
  }

  @RabbitListener(queues = [Queueing.QUEUE_RESULT])
  private fun receiveResult(message: String)
  {
    val analysisResultMessage = message.fromJson<AnalysisResultMessage>()

    val analysisResultId = storeAnalysisResult(analysisResultMessage)

    val analysisResult = loadAnalysisResult(analysisResultId)
  }

  private fun storeAnalysisResult(analysisResultMessage: AnalysisResultMessage): Long
  {
    // AnalysisResult
    val analysisResult = this.analysisResultRepository.save(analysisResultMessage.toEntity())

    val timestamp = analysisResult.modified

    // WebpageResult
    for(webpageResultMessage in analysisResultMessage.webpages)
    {
      val webpageResult = this.webpageResultRepository.save(webpageResultMessage.toEntity(analysisResult, timestamp))

      // Rule
      for(ruleMessage in webpageResultMessage.rules)
      {
        val rule = this.ruleRepository.save(ruleMessage.toEntity(webpageResult, timestamp))

        // Check
        for(checkMessage in ruleMessage.checks)
        {
          val check = this.checkRepository.save(checkMessage.toEntity(rule, timestamp))

          val checkElements = mutableListOf<Pair<CheckElementMessage, CheckElementEntity>>()

          // ViolatingCheckElements
          check.violatingElements.addAll(checkMessage.violatingElements.map { checkElementMessage ->
            checkElementMessage.toEntity(timestamp)
              .also { checkElements.add(Pair(checkElementMessage, it)) }
          })

          // IncompleteCheckElements
          check.incompleteElements.addAll(checkMessage.incompleteElements.map { checkElementMessage ->
            checkElementMessage.toEntity(timestamp)
              .also { checkElements.add(Pair(checkElementMessage, it)) }
          })

          this.checkRepository.save(check)

          // Elements
          for(checkElement in checkElements)
          {
            for(element in checkElement.first.relatedElements)
            {
              this.elementRepository.save(element.toEntity(checkElement.second, timestamp))
            }
          }
        }
      }
    }

    return analysisResult.analysisResultId
  }

  public fun loadAnalysisResult(analysisResultId: Long): AnalysisResultEntity
  {
    return this.analysisResultRepository.findById(analysisResultId).orElseThrow()
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
