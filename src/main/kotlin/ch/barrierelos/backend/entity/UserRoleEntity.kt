package ch.barrierelos.backend.entity

import ch.barrierelos.backend.model.enums.RoleEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "user_role")
public class UserRoleEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var userRoleId: Long = 0,
  public var userFk: Long,
  @Enumerated(EnumType.STRING)
  public var role: RoleEnum,
  public var modified: Timestamp = Timestamp(0),
)
