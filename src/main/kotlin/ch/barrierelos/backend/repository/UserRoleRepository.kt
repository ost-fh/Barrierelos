package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.UserRoleEntity

public interface UserRoleRepository : Repository<UserRoleEntity>
{
  public fun findAllByUserFk(userFk: Long): List<UserRoleEntity>
}
