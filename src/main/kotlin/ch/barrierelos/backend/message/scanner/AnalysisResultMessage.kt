package ch.barrierelos.backend.message.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class AnalysisResultMessage
(
  public val modelVersion: String,
  public val website: String,
  public var scanTimestamp: String,
  public val scanStatus: ScanStatusEnum,
  public val errorMessage: String? = null,
  public val webpages: MutableSet<WebpageResultMessage>,
)
