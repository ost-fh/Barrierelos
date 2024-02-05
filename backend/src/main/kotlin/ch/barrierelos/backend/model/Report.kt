package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Report
(
  public var id: Long = 0,
  public var userId: Long,
  public var reason: ReasonEnum,
  public var state: StateEnum = StateEnum.OPEN,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
