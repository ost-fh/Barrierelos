package ch.barrierelos.backend.entity

import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_scan")
public class WebsiteScanEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteScanId: Long = 0,
  @OneToOne(optional = false, cascade = [CascadeType.MERGE])
  @JoinColumn(name = "website_fk")
  public var website: WebsiteEntity,
  @OneToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "website_statistic_fk")
  public var websiteStatistic: WebsiteStatisticEntity? = null,
  @OneToOne(cascade = [CascadeType.MERGE])
  @JoinColumn(name = "website_result_fk")
  public var websiteResult: WebsiteResultEntity? = null,
  @OneToMany(mappedBy = "websiteScan", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  public var webpageScans: MutableSet<WebpageScanEntity>,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
