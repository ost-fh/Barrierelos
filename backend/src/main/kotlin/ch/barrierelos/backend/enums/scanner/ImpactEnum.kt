package ch.barrierelos.backend.enums.scanner

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class ImpactEnum
{
  @SerialName("minor")
  MINOR,
  @SerialName("moderate")
  MODERATE,
  @SerialName("serious")
  SERIOUS,
  @SerialName("critical")
  CRITICAL
}
