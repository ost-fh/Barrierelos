package ch.barrierelos.backend.util

import ch.barrierelos.backend.constant.Credential.MIN_LENGTH_PASSWORD
import ch.barrierelos.backend.constant.Credential.MIN_LENGTH_USERNAME

public fun String.isValidEmail(): Boolean
{
  return this.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9.-]+\$".toRegex())
}

public fun String.isValidUsername(): Boolean
{
  return this.length >= MIN_LENGTH_USERNAME && !this.contains('@')
}

public fun String.isValidPassword(): Boolean
{
  return this.length >= MIN_LENGTH_PASSWORD
}
