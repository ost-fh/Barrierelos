package ch.barrierelos.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus
public class ReferenceNotExistsException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernameException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDomainException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPathException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUrlException(url: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Url (${url}) is not valid.")

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UrlNotMatchingWebsiteDomainException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAuthorizationException : ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorization.")

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoRoleException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException(message: String) : ResponseStatusException(HttpStatus.CONFLICT, message)

@ResponseStatus
public class InvalidStateException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

@ResponseStatus
public class InvalidArgumentException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)
