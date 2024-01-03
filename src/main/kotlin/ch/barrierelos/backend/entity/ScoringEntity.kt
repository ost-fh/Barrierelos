package ch.barrierelos.backend.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.sql.Timestamp

@Entity
public class ScoringEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageResultId: Long = 0,
  public var score: Double,
  public var weight: Long,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
