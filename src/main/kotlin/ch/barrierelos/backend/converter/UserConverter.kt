package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.model.User
import java.sql.Timestamp

public fun User.toEntity(): UserEntity
{
  return UserEntity(
    userId = this.id,
    username = this.username,
    firstname = this.firstname,
    lastname = this.lastname,
    email = this.email,
    password = this.password,
    issuer = this.issuer,
    subject = this.subject,
    modified = Timestamp(this.modified),
  )
}

public fun UserEntity.toModel(): User
{
  return User(
    id = this.userId,
    username = this.username,
    firstname = this.firstname,
    lastname = this.lastname,
    email = this.email,
    password = this.password,
    issuer = this.issuer,
    subject = this.subject,
    modified = this.modified.time,
  )
}
