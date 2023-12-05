package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebpageScan
(
  public var id: Long = 0,
  public var websiteScanId: Long,
  public var webpageId: Long,
  public var webpageStatisticId: Long,
  public var webpageResultId: Long,
  public var userId: Long,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
