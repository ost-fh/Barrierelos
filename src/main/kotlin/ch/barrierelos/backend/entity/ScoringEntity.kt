package ch.barrierelos.backend.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.sql.Timestamp

@Entity
public class ScoringEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var scoringId: Long = 0,
    public var modified: Timestamp = Timestamp(0),
)
