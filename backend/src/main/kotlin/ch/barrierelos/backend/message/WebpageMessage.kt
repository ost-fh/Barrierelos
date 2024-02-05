package ch.barrierelos.backend.message

import kotlinx.serialization.Serializable

@Serializable
public data class WebpageMessage
  (
  public var websiteId: Long,
  public var url: String,
)
