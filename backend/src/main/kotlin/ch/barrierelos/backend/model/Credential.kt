package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class Credential
(
  public var id: Long = 0,
  public var userId: Long,
  public var password: String? = null,
  public var issuer: String? = null,
  public var subject: String? = null,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
