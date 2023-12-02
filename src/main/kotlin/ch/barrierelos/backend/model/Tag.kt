package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class Tag
(
  public var id: Long = 0,
  public var name: String,
)
