package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "report")
public class ReportEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var reportId: Long = 0,
  public var userFk: Long,
  @Enumerated(EnumType.STRING)
  public var reason: ReasonEnum,
  @Enumerated(EnumType.STRING)
  public var status: StatusEnum,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
