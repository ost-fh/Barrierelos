package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.CategoryEnum
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
  public var userFk: Long,
  public var domain: String,
  public var url: String,
  public var category: CategoryEnum,
  public var status: StatusEnum,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
