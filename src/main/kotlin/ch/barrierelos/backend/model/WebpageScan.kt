package ch.barrierelos.backend.model

import ch.barrierelos.backend.model.scanner.WebpageResult

public data class WebpageScan
(
  public var id: Long = 0,
  public var webpage: Webpage,
  public var webpageStatistic: WebpageStatistic? = null,
  public var webpageResult: WebpageResult? = null,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
