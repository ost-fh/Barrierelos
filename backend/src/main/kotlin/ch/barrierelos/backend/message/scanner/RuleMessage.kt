package ch.barrierelos.backend.message.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class RuleMessage(
  public val id: String,
  public val description: String,
  public val axeUrl: String,
  public val wcagReferences: WcagReferencesMessage? = null,
  public val checks: MutableSet<CheckMessage>,
)
