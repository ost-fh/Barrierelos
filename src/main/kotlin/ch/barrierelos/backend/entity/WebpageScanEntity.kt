package ch.barrierelos.backend.entity

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_scan")
public class WebpageScanEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageScanId: Long = 0,
  @ManyToOne(optional = false)
  @JoinColumn(name = "website_scan_fk")
  public var websiteScan: WebsiteScanEntity,
  @OneToOne(optional = false, cascade = [CascadeType.MERGE])
  @JoinColumn(name = "webpage_fk")
  public var webpage: WebpageEntity,
  @OneToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "webpage_statistic_fk")
  public var webpageStatistic: WebpageStatisticEntity? = null,
  @OneToOne(cascade = [CascadeType.MERGE])
  @JoinColumn(name = "webpage_result_fk")
  public var webpageResult: WebpageResultEntity? = null,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
