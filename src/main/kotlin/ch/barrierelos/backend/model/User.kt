package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class User
(
  public var id: Long = 0,
  public var username: String,
  public var firstname: String,
  public var lastname: String,
  public var email: String,
  public var password: String?,
  public var issuer: String?,
  public var subject: String?,
  public var modified: Long = System.currentTimeMillis(),
)
