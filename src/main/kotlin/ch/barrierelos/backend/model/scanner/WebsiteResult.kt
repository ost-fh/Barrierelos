package ch.barrierelos.backend.model.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteResult(
  public val id: Long,
  public val modelVersion: String,
  public val scanJob: ScanJob,
  public val domain: String,
  public val scanStatus: ScanStatusEnum,
  public val scanTimestamp: Instant,
  public val errorMessage: String? = null,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
