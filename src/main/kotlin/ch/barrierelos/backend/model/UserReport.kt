package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class UserReport
(
  public var id: Long = 0,
  public var userId: Long,
  public var reportId: Long,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
