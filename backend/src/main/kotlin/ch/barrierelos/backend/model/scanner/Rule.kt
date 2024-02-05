package ch.barrierelos.backend.model.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class Rule(
  public val id: Long,
  public val code: String,
  public val description: String,
  public val axeUrl: String,
  public val wcagReferences: WcagReferences?,
  public val checks: MutableSet<Check>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
