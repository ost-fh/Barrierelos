package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteScan(
  public var id: Long = 0,
  public var websiteId: Long,
  val websiteStatisticFk: Long,
  public var userId: Long,
  public var scanResultId: Long,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
