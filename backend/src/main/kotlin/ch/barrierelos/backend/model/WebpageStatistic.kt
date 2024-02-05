package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebpageStatistic(
  public var id: Long = 0,
  public var score: Double,
  public var weight: Double,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
