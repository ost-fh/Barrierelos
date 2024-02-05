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
  @ManyToOne
  @JoinColumn(name = "userFk", referencedColumnName = "userId", nullable = false)
  public var user: UserEntity,
  @Column(unique = true)
  public var url: String,
  @Column(unique = true)
  public var domain: String,
  @Enumerated(EnumType.STRING)
  public var category: CategoryEnum,
  @Enumerated(EnumType.STRING)
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  public var score: Double? = null,
  @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
  @JoinColumn(name = "websiteFk", referencedColumnName = "websiteId")
  public var tags: MutableSet<WebsiteTagEntity>,
  public var deleted: Boolean = false,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
