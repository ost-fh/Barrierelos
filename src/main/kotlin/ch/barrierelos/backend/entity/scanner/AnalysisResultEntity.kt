package ch.barrierelos.backend.entity.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "analysis_result")
public class AnalysisResultEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var analysisResultId: Long = 0,
  public var modelVersion: String,
  public var website: String,
  public var scanTimestamp: Timestamp,
  @Enumerated(EnumType.STRING)
  public var scanStatus: ScanStatusEnum,
  public var errorMessage: String? = null,
  @JoinColumn(name = "analysisJobFk", referencedColumnName = "analysisJobId")
  @OneToOne(fetch = FetchType.EAGER)
  public var analysisJob: AnalysisJobEntity? = null,
  @OneToMany(mappedBy = "analysisResult", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  public var webpages: MutableSet<WebpageResultEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(0),
)
