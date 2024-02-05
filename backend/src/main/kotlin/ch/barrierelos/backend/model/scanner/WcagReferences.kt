package ch.barrierelos.backend.model.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class WcagReferences(
  public var id: Long,
  public val version: String,
  public val level: String,
  public val criteria: MutableSet<String>,
)
