package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.User

fun createUserModel() = User(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  password = "password",
  issuer = "issuer",
  subject = "subject",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = 5000,
)
