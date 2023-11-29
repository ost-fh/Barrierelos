package ch.barrierelos.backend.service

import ch.barrierelos.backend.model.scanner.AnalysisResult
import ch.barrierelos.backend.repository.ScoringRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class ScoringService
{
  private val logger: Logger = LoggerFactory.getLogger(ScoringService::class.java)

  @Autowired
  private lateinit var scoringRepository: ScoringRepository

  /**
   * Gets called after the analysis result from the scanner is stored in the database.
   */
  public fun onReceiveResult(analysisResult: AnalysisResult)
  {
    val scorings = scoringRepository.calculateWebpageScores(analysisResult.id)
    val websiteTotalCount = scorings.sumOf { it.totalCount }
    val websiteScore = scorings.sumOf { it.score * (it.totalCount / websiteTotalCount.toDouble()) }
    logger.info("Website (${analysisResult.website} score: $websiteScore")
    scorings.forEach { logger.info("Webpage (${it.path}) score: ${it.score}") }
    return
  }
}
