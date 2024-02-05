package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
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
  public var state: StateEnum = StateEnum.OPEN,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
