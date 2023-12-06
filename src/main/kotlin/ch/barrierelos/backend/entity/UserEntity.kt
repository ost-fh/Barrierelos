package ch.barrierelos.backend.entity

import ch.barrierelos.backend.enums.RoleEnum
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
  @Enumerated(EnumType.STRING)
  public var roles: MutableSet<RoleEnum>,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
