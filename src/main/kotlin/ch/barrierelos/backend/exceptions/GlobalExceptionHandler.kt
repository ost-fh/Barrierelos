package ch.barrierelos.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
public class GlobalExceptionHandler : ResponseEntityExceptionHandler()
{
  @ExceptionHandler(NoSuchElementException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public fun noSuchElementException(): ResponseStatusException  = ResponseStatusException(HttpStatus.NOT_FOUND)
}
