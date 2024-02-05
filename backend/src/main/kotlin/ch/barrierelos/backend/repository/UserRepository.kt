package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.UserEntity
import org.springframework.data.jpa.repository.Query

public interface UserRepository : Repository<UserEntity>
{
  public fun findByUsername(username: String): UserEntity?

  @Query("SELECT u FROM UserEntity u, CredentialEntity c WHERE u.userId = c.userFk AND c.issuer = ?1 AND c.subject = ?2")
  public fun findByIssuerAndSubject(issuer: String, subject: String): UserEntity?

  public fun existsByUsername(username: String): Boolean

  public fun findAllByUserIdIn(userIds: Collection<Long>): Set<UserEntity>
}
