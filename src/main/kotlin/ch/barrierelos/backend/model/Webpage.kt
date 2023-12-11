package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Webpage
(
  public var id: Long = 0,
  public var websiteId: Long,
  public var userId: Long,
  public var path: String,
  public var url: String,
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  public var deleted: Boolean = false,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
