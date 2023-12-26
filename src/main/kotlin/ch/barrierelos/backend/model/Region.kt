package ch.barrierelos.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
public data class Region(
  @Id
  public var id: Long = 0,
  public var name: String,
  public var score: Double,
)
