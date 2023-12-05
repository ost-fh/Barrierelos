package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_scan")
public class WebpageScanEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageScanId: Long = 0,
  public var websiteScanFk: Long,
  public var webpageFk: Long,
  public var webpageStatisticFk: Long,
  public var webpageResultFk: Long,
  public var userFk: Long,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
