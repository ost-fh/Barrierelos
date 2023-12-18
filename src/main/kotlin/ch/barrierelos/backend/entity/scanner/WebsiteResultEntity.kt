package ch.barrierelos.backend.entity.scanner

import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_result")
public class WebsiteResultEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteResultId: Long = 0,
  public var modelVersion: String,
  public var domain: String,
  public var scanTimestamp: Timestamp,
  @Enumerated(EnumType.STRING)
  public var scanStatus: ScanStatusEnum,
  public var errorMessage: String? = null,
  @OneToOne(cascade = [CascadeType.MERGE], orphanRemoval = true)
  @JoinColumn(name = "scanJobFk", referencedColumnName = "scanJobId")
  public var scanJob: ScanJobEntity,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
