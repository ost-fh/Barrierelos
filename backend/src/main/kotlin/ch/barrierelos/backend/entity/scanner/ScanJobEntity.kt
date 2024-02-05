package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "scan_job")
public class ScanJobEntity
  (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var scanJobId: Long = 0,
  public var websiteFk: Long,
  public var userFk: Long,
  public val modelVersion: String,
  public val jobTimestamp: Timestamp,
  public val domain: String,
  @Suppress("JpaAttributeTypeInspection", "RedundantSuppression")
  public val webpages: MutableSet<String>,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
