package ch.barrierelos.backend.message.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class ElementMessage
(
  public val target: String,
  public val html: String,
)
