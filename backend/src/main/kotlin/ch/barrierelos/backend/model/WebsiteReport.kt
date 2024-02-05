package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteReport
(
  public var id: Long = 0,
  public var websiteId: Long,
  public var report: Report,
)
