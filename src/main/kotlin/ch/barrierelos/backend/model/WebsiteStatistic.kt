package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteStatistic(
  public var id: Long = 0,
  public var websiteId: Long,
  public var userId: Long,
  public var score: Double,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
