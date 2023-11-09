package ch.barrierelos.backend.model.scanner

import kotlinx.serialization.Serializable

@Serializable
public data class AnalysisJob
(
  public val id: Long = 0,
  public val modelVersion: String,
  public val websiteBaseUrl: String,
  public val webpagePaths: MutableSet<String>,
  public var modified: Long = System.currentTimeMillis(),
)
