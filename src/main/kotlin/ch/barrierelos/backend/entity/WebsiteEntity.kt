package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.StatusEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website")
public class WebsiteEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteId: Long = 0,
  public var userFk: Long,
  public var domain: String,
  public var url: String,
  public var category: CategoryEnum,
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  @OneToMany(orphanRemoval=true, cascade=[CascadeType.ALL], fetch=FetchType.EAGER)
  @JoinColumn(name="websiteFk")
  public var tags: MutableSet<WebsiteTagEntity>,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
