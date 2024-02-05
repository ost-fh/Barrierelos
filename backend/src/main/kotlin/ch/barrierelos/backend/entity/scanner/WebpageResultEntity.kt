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
  @JoinColumn(name = "website_result_fk", nullable = false)
  public var websiteResult: WebsiteResultEntity,
  public var url: String,
  @Enumerated(EnumType.STRING)
  public var scanStatus: ScanStatusEnum,
  public var errorMessage: String? = null,
  @OneToMany(mappedBy = "webpageResult", orphanRemoval = true, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
  public var rules: MutableSet<RuleEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
