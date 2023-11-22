package ch.barrierelos.backend.helper

import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.enums.RoleEnum
import java.sql.Timestamp

fun createUserEntity() = UserEntity(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  password = "password",
  issuer = "issuer",
  subject = "subject",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = Timestamp(5000),
)
