package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.UserEntity
import org.springframework.data.jpa.repository.Query

public interface UserRepository : Repository<UserEntity>
{
  public fun findByUserId(id: Long): UserEntity?

  public fun findByUsername(username: String): UserEntity?

  public fun findByIssuerAndSubject(issuer: String, subject: String): UserEntity?

  @Query("SELECT u.password FROM UserEntity u WHERE u.userId = ?1")
  public fun getPasswordById(userId: Long): String
}
