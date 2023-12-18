package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "user_report")
public class UserReportEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var userReportId: Long = 0,
  public var userFk: Long,
  public var reportFk: Long,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
