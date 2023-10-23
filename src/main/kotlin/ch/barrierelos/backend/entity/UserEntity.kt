package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "user")
public class UserEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var userId: Long = 0,
  public var username: String,
  public var password: String,
  public var firstname: String,
  public var lastname: String,
  public var email: String,
  public var modified: Timestamp = Timestamp(0),
)
