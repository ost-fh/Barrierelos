package ch.barrierelos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_report")
public class UserReportEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var userReportId: Long = 0,
  public var userFk: Long,
  @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
  @JoinColumn(name = "report_fk", referencedColumnName = "reportId")
  public var report: ReportEntity,
)
