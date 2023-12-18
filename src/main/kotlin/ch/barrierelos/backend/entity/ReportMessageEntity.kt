package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "report_message")
public class ReportMessageEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var reportMessageId: Long = 0,
  public var userFk: Long,
  public var reportFk: Long,
  public var message: String,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
