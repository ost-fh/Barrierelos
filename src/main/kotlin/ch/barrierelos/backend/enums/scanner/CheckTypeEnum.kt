package ch.barrierelos.backend.enums.scanner

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class CheckTypeEnum
{
  @SerialName("any")
  ANY,
  @SerialName("all")
  ALL,
  @SerialName("none")
  NONE
}
