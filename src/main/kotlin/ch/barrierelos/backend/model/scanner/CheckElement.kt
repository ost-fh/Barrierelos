package ch.barrierelos.backend.model.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class CheckElement
(
  public val id: Long,
  public val target: String,
  public val html: String,
  public val issueDescription: String,
  public val data: String,
  public val relatedElements: MutableSet<Element>,
  public var modified: Long = System.currentTimeMillis(),
)
