package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_scan")
public class WebsiteScanEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteScanId: Long = 0,
  public var websiteFk: Long,
  public var websiteResultFk: Long,
  public var websiteStatisticFk: Long,
  public var userFk: Long,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
