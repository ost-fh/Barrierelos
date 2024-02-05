package ch.barrierelos.backend.model.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class WebpageResult
(
  public val id: Long,
  public val url: String,
  public val scanStatus: ScanStatusEnum,
  public val errorMessage: String? = null,
  public val rules: MutableSet<Rule>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
