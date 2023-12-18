package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_report")
public class WebsiteReportEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteReportId: Long = 0,
  public var websiteFk: Long,
  public var reportFk: Long,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
