package ch.barrierelos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "webpage_report")
public class WebpageReportEntity
  (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageReportId: Long = 0,
  public var webpageFk: Long,
  @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
  @JoinColumn(name = "report_fk", referencedColumnName = "reportId")
  public var report: ReportEntity,
)
