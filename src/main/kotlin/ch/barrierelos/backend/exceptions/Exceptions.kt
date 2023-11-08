package ch.barrierelos.backend.exceptions

public class InvalidCredentialsException(message: String) : Exception(message)

public class InvalidEmailException(message: String) : Exception(message)

public class NoRoleException(message: String) : Exception(message)

public class UserAlreadyExistsException(message: String) : Exception(message)
