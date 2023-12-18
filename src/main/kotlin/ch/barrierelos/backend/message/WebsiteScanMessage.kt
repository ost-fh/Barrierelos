package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.WebsiteStatistic
import ch.barrierelos.backend.model.scanner.WebsiteResult
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteScanMessage(
  public var id: Long = 0,
  public var website: Website,
  public var websiteStatistic: WebsiteStatistic? = null,
  public var websiteResult: WebsiteResult? = null,
  public var webpageScans: MutableSet<WebpageScanMessage>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
