package ch.barrierelos.backend.helper

import ch.barrierelos.backend.entity.*
import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import java.sql.Timestamp

fun createUserEntity() = UserEntity(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createCredentialEntity() = CredentialEntity(
  userFk = 1,
  password = "password",
  issuer = "issuer",
  subject = "subject",
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createTagEntity() = TagEntity(
  name = "Something"
)
