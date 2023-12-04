package ch.barrierelos.backend.message.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteResultMessage(
  public val modelVersion: String,
  public val jobId: Long,
  public val website: String,
  public var scanTimestamp: Instant,
  public val scanStatus: ScanStatusEnum,
  public val errorMessage: String? = null,
  public val webpages: MutableSet<WebpageResultMessage>,
)
