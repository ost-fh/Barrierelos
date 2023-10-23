package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.UserRoleEntity
import ch.barrierelos.backend.model.UserRole
import java.sql.Timestamp

public fun UserRole.toEntity(): UserRoleEntity
{
  return UserRoleEntity(
    userRoleId = this.id,
    userFk = this.userId,
    role = this.role,
    modified = Timestamp(this.modified),
  )
}

public fun UserRoleEntity.toModel(): UserRole
{
  return UserRole(
    id = this.userRoleId,
    userId = this.userFk,
    role = this.role,
    modified = this.modified.time,
  )
}
