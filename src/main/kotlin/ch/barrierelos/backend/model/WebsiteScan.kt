package ch.barrierelos.backend.model

import ch.barrierelos.backend.model.scanner.WebsiteResult
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteScan(
  public var id: Long = 0,
  public var website: Website,
  public var websiteStatistic: WebsiteStatistic? = null,
  public var websiteResult: WebsiteResult? = null,
  public var webpageScans: MutableSet<WebpageScan>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
