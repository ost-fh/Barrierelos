package ch.barrierelos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "website_report")
public class WebsiteReportEntity
  (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteReportId: Long = 0,
  public var websiteFk: Long,
  @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
  @JoinColumn(name = "report_fk", referencedColumnName = "reportId")
  public var report: ReportEntity,
)
