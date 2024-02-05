package ch.barrierelos.backend.model.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class Element
(
  public val id: Long,
  public val target: String,
  public val html: String,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
