package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User
import kotlinx.serialization.Serializable

@Serializable
public data class RegistrationMessage
(
  public var user: User,
  public var credential: Credential,
)
