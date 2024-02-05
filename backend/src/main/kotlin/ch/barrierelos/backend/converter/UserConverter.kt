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
    roles = this.roles,
    deleted = this.deleted,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
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
    roles = this.roles,
    deleted = this.deleted,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun UserEntity.toModel(user: User): User
{
  return user.apply {
    id = this@toModel.userId
    username = this@toModel.username
    firstname = this@toModel.firstname
    lastname = this@toModel.lastname
    email = this@toModel.email
    roles = this@toModel.roles
    deleted = this@toModel.deleted
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<User>.toEntities(): MutableSet<UserEntity>
{
  return this.map { user -> user.toEntity() }.toMutableSet()
}

public fun Collection<UserEntity>.toModels(): MutableSet<User>
{
  return this.map { user -> user.toModel() }.toMutableSet()
}
