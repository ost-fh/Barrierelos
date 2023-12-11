package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.StatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage")
public class WebpageEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageId: Long = 0,
  public var websiteFk: Long,
  public var userFk: Long,
  public var path: String,
  @Column(unique = true)
  public var url: String,
  @Enumerated(EnumType.STRING)
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
