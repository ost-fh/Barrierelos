package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_statistic")
public class WebsiteStatisticEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteStatisticId: Long = 0,
  public var score: Double,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
