package ch.barrierelos.backend.entity

import ch.barrierelos.backend.model.enums.RoleEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "user")
public class UserEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var userId: Long = 0,
  @Column(unique=true)
  public var username: String,
  public var firstname: String,
  public var lastname: String,
  public var email: String,
  public var password: String?,
  public var issuer: String?,
  public var subject: String?,
  @Enumerated(EnumType.STRING)
  public var roles: MutableSet<RoleEnum>,
  public var modified: Timestamp = Timestamp(0),
)
