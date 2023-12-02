package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteReport
(
  public var id: Long = 0,
  public var websiteId: Long,
  public var reportId: Long,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
