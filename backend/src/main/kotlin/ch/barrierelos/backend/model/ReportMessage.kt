package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class ReportMessage
(
  public var id: Long = 0,
  public var reportId: Long,
  public var userId: Long,
  public var message: String,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
