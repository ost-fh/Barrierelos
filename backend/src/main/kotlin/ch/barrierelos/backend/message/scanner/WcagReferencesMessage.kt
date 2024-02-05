package ch.barrierelos.backend.message.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class WcagReferencesMessage(
  public var id: Long = 0,
  public val version: String,
  public val level: String,
  public val criteria: MutableSet<String>,
)
