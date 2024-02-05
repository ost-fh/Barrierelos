package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.model.WebpageStatistic
import ch.barrierelos.backend.model.scanner.WebpageResult
import kotlinx.serialization.Serializable

@Serializable
public data class WebpageScanMessage(
  public var id: Long = 0,
  public var webpage: Webpage,
  public var webpageStatistic: WebpageStatistic? = null,
  public var webpageResult: WebpageResult? = null,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
