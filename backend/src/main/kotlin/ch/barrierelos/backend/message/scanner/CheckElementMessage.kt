package ch.barrierelos.backend.message.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class CheckElementMessage
(
  public val target: String,
  public val html: String,
  public val issueDescription: String,
  public val data: String,
  public val relatedElements: MutableSet<ElementMessage>,
)
