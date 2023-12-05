package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_statistic")
public class WebpageStatisticEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var webpageStatisticId: Long = 0,
  public var webpageFk: Long,
  public var websiteStatisticFk: Long,
  public var userFk: Long,
  public var score: Double,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
