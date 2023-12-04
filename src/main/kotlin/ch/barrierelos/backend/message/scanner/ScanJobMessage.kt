package ch.barrierelos.backend.message.scanner

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class ScanJobMessage(
  public val jobId: Long,
  public val modelVersion: String,
  public var jobTimestamp: Instant,
  public val locale: String,
  public val websiteBaseUrl: String,
  public val webpagePaths: MutableSet<String>,
)
