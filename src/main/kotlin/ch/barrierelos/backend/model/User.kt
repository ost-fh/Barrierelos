package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.RoleEnum
import kotlinx.serialization.Serializable

@Serializable
public data class User
(
  public var id: Long = 0,
  public var username: String,
  public var firstname: String,
  public var lastname: String,
  public var email: String,
  public var roles: MutableSet<RoleEnum>,
  public var deleted: Boolean = false,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
