package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.CredentialEntity

public interface CredentialRepository : Repository<CredentialEntity>
{
  public fun findByUserFk(userId: Long): CredentialEntity?

  public fun deleteByUserFk(userId: Long)

  public fun existsByUserFk(userId: Long): Boolean
}
