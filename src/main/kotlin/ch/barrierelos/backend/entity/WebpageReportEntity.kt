package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_report")
public class WebpageReportEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageReportId: Long = 0,
  public var webpageFk: Long,
  public var reportFk: Long,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
