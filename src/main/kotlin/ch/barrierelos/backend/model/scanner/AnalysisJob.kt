package ch.barrierelos.backend.model.scanner

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class AnalysisJob(
  public val id: Long = 0,
  public val modelVersion: String,
  public val jobTimestamp: Instant = Clock.System.now(),
  public val locale: String,
  public val websiteBaseUrl: String,
  public val webpagePaths: MutableSet<String>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
