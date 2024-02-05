package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebpageReport
(
  public var id: Long = 0,
  public var webpageId: Long,
  public var report: Report,
)
