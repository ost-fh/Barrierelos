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
  public val modelVersion: String,
  public val locale: String,
  public val websiteBaseUrl: String,
  @Suppress("JpaAttributeTypeInspection", "RedundantSuppression")
  public val webpagePaths: MutableSet<String>,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
