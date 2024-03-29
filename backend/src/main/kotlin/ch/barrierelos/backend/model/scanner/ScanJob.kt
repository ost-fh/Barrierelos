package ch.barrierelos.backend.model.scanner

import ch.barrierelos.backend.constant.Scanner
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class ScanJob(
  public val id: Long = 0,
  public val websiteId: Long,
  public val userId: Long,
  public val modelVersion: String = Scanner.MODEL_VERSION,
  public val jobTimestamp: Instant,
  public val domain: String,
  public val webpages: MutableSet<String>,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
