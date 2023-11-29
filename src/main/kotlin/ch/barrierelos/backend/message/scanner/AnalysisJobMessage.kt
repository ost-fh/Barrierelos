package ch.barrierelos.backend.message.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class AnalysisJobMessage(
  public val modelVersion: String,
  public val jobId: Long,
  public var jobTimestamp: String,
  public val websiteBaseUrl: String,
  public val webpagePaths: MutableSet<String>,
)
