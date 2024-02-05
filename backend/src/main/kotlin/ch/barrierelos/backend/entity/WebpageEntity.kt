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
  @ManyToOne
  @JoinColumn(name = "website_fk", referencedColumnName = "websiteId", nullable = false)
  public var website: WebsiteEntity,
  @ManyToOne
  @JoinColumn(name = "user_fk", referencedColumnName = "userId", nullable = false)
  public var user: UserEntity,
  @Column(unique = true)
  public var url: String,
  public var displayUrl: String,
  @Enumerated(EnumType.STRING)
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  public var deleted: Boolean = false,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
