package ch.barrierelos.backend.entity.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_result")
public class WebpageResultEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageResultId: Long = 0,
  @ManyToOne
  @JoinColumn(name="analysis_result_fk", nullable=false)
  public var analysisResult: AnalysisResultEntity,
  public var path: String,
  @Enumerated(EnumType.STRING)
  public var scanStatus: ScanStatusEnum,
  public var errorMessage: String? = null,
  @OneToMany(mappedBy = "webpageResult", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  public var rules: MutableSet<RuleEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(0),
)
