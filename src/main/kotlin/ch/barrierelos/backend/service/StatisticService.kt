package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toMessage
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.model.*
import ch.barrierelos.backend.model.scanner.WebpageResult
import ch.barrierelos.backend.model.scanner.WebsiteResult
import ch.barrierelos.backend.repository.*
import ch.barrierelos.backend.util.orThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class StatisticService
{
  @Autowired
  private lateinit var scoringRepository: ScoringRepository

  @Autowired
  private lateinit var websiteScanRepository: WebsiteScanRepository

  @Autowired
  private lateinit var webpageScanRepository: WebpageScanRepository

  @Autowired
  private lateinit var websiteRepository: WebsiteRepository

  @Autowired
  private lateinit var webpageRepository: WebpageRepository


  public fun addWebsiteScan(website: Website): WebsiteScan
  {
    val websiteScan = WebsiteScan(
      website = website,
      webpageScans = mutableSetOf()
    )
    return websiteScanRepository.save(websiteScan.toEntity()).toModel()
  }

  public fun addWebpageScan(webpage: Webpage): WebpageScan
  {
    val websiteScan = websiteScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated(webpage.website.id)
      .orThrow(NoSuchElementException("WebsiteScan for this webpage does not exist.")).toModel()

    val webpageScan = WebpageScan(
      webpage = webpage,
    )

    return webpageScanRepository.save(webpageScan.toEntity(websiteScan.toEntity())).toModel()
  }

  /**
   * Gets called after the result from the scanner is stored in the database.
   */
  public fun onReceiveResult(websiteResult: WebsiteResult, webpageResults: MutableSet<WebpageResult>)
  {
    val website = websiteRepository.findById(websiteResult.scanJob.websiteId)
      .orThrow(NoSuchElementException("Website with this id does not exist."))
      .toModel()

    var websiteScan = websiteScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated(website.id)
      .orThrow(NoSuchElementException("WebsiteScan with this websiteId does not exist."))
      .toModel()

    val scorings = scoringRepository.calculateWebpageScores(websiteResult.id)
    var websiteWeight = 0L
    if(scorings.isNotEmpty())
    {
      websiteWeight = scorings.sumOf { it.weight }
      val websiteScore = scorings.sumOf { it.score * (it.weight / websiteWeight.toDouble()) }
      website.score = websiteScore
      websiteScan.websiteStatistic = WebsiteStatistic(
        score = websiteScore,
      )
    }
    website.status = StatusEnum.READY

    websiteScan.website = website
    websiteScan.websiteResult = websiteResult
    websiteScan = websiteScanRepository.save(websiteScan.toEntity()).toModel()

    val webpages = webpageRepository.findAllByWebsiteWebsiteId(websiteResult.scanJob.websiteId).toModels()
    val webpageScans = webpageScanRepository.findAllByWebsiteScanWebsiteScanId(websiteScan.id)
      .map { it.toModel() }.toMutableSet()

    webpageScans.forEach {
      val webpage = webpages.find { webpage -> webpage.url == it.webpage.url }
        ?: throw NoSuchElementException("Webpage for this webpageScan does not exist.")
      val webpageResult = webpageResults.find { webpageResult -> webpageResult.url == it.webpage.url }
      val scoring = scorings.find { scoring -> scoring.webpageResultId == webpageResult?.id }

      if(scoring != null)
      {
        webpage.status = StatusEnum.READY
        it.webpageStatistic = WebpageStatistic(
          score = scoring.score,
          weight = scoring.weight / websiteWeight.toDouble(),
        )
      }

      it.webpage = webpage
      it.webpageResult = webpageResult
    }

    webpageScanRepository.saveAll(webpageScans.map { it.toEntity(websiteScan.toEntity(), websiteResult) }).map { it.toModel() }
  }

  public fun getWebsiteScan(websiteId: Long): WebsiteScanMessage
  {
    return websiteScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated(websiteId)
      .orThrow(NoSuchElementException("WebsiteScan with this websiteId does not exist.")).toModel().toMessage()
  }
}
