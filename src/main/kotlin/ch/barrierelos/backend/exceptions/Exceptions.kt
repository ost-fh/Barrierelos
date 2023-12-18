package ch.barrierelos.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus
public class ReferenceNotExistsException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDomainException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPathException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUrlException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAuthorizationException : ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorization.")

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoRoleException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException(message: String) : ResponseStatusException(HttpStatus.CONFLICT, message)

@ResponseStatus
public class InvalidStateException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)
