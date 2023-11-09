package ch.barrierelos.backend.message.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class WebpageResultMessage
(
  public val path: String,
  public val scanStatus: ScanStatusEnum,
  public val errorMessage: String? = null,
  public val rules: MutableSet<RuleMessage>,
)
