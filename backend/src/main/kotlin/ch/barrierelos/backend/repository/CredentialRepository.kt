package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.CredentialEntity
import org.springframework.transaction.annotation.Transactional

public interface CredentialRepository : Repository<CredentialEntity>
{
  public fun findByUserFk(userId: Long): CredentialEntity?

  @Transactional
  public fun deleteByUserFk(userId: Long)

  public fun existsByUserFk(userId: Long): Boolean
}
