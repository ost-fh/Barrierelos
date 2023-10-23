package ch.barrierelos.backend.model

import ch.barrierelos.backend.model.enums.RoleEnum
import kotlinx.serialization.Serializable

@Serializable
public data class UserRole
(
  public var id: Long = 0,
  public var userId: Long,
  public var role: RoleEnum,
  public var modified: Long = System.currentTimeMillis(),
)
