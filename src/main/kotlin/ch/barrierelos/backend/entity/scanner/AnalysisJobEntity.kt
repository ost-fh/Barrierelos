package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "analysis_job")
public class AnalysisJobEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var analysisJobId: Long = 0,
  public val modelVersion: String,
  public val websiteBaseUrl: String,
  @Suppress("JpaAttributeTypeInspection")
  public val webpagePaths: MutableSet<String>,
  public var modified: Timestamp = Timestamp(0),
)
