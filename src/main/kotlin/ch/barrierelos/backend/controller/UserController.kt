package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.USER
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.message.RegistrationMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.service.UserService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class UserController
{
  @Autowired
  private lateinit var userService: UserService

  @PostMapping(value = [USER], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addUser(@RequestBody registration: RegistrationMessage): ResponseEntity<User>
  {
    registration.user.id = 0

    val user: User = this.userService.addUser(registration.user, registration.credential)

    return ResponseEntity.status(HttpStatus.CREATED).body(user)
  }
  
  @PutMapping(value = ["$USER/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User>
  {
    user.id = id

    this.userService.updateUser(user)
    
    return ResponseEntity.status(HttpStatus.OK).body(user)
  }
  
  @GetMapping(value = [USER], produces = [MediaType.JSON])
  public fun getUsers(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<User>>
  {
    val userPage = this.userService.getUsers(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(userPage.toHeaders()).body(userPage.content)
  }
  
  @GetMapping(value = ["$USER/{id}"], produces = [MediaType.JSON])
  public fun getUser(@PathVariable id: Long): ResponseEntity<User>
  {
    val user: User = this.userService.getUser(id)
    
    return ResponseEntity.status(HttpStatus.OK).body(user)
  }
  
  @DeleteMapping(value = ["$USER/{id}"], produces = [MediaType.JSON])
  public fun deleteUser(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.userService.deleteUser(id)
    
    return ResponseEntity.status(HttpStatus.OK).build()
  }

  @GetMapping(value = ["$USER/login"], produces = [MediaType.JSON])
  public fun login(): ResponseEntity<User>
  {
    val user: User = Security.getUser()

    return ResponseEntity.status(HttpStatus.OK).body(user)
  }
}
