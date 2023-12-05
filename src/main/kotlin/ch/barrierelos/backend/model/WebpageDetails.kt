package ch.barrierelos.backend.model

import ch.barrierelos.backend.model.scanner.WebpageResult
import kotlinx.serialization.Serializable

@Serializable
public data class WebpageDetails(
  public var id: Long = 0,
  public var webpage: Webpage,
  public var statistics: WebpageStatistic? = null,
  public var scanResult: WebpageResult? = null,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
