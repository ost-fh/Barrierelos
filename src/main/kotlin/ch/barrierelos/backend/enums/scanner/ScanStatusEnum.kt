package ch.barrierelos.backend.enums.scanner

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class ScanStatusEnum
{
  @SerialName("success")
  SUCCESS,
  @SerialName("failed")
  FAILED
}
