package ch.barrierelos.backend.model.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class AnalysisResult
(
  public val id: Long,
  public val modelVersion: String,
  public val website: String,
  public val scanStatus: ScanStatusEnum,
  public val errorMessage: String? = null,
  public val webpages: MutableSet<WebpageResult>,
  public var modified: Long = System.currentTimeMillis(),
)
