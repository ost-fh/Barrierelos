package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User

fun createUserModel() = User(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = 5000,
  created = 5000,
)

fun createCredentialModel() = Credential(
  password = "password",
  userId = 1,
  issuer = "issuer",
  subject = "subject",
  modified = 5000,
  created = 5000,
)
