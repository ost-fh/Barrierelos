package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.USER
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.parameter.DefaultParameters
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
  public fun addUser(@RequestBody user: User): ResponseEntity<User>
  {
    user.id = 0

    val savedUser: User = this.userService.addUser(user)

    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
  }
  
  @PutMapping(value = ["$USER/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateUser(@PathVariable id: Long, @RequestBody user: User, @RequestParam changeCredentials: Boolean = false): ResponseEntity<User>
  {
    user.id = id

    val savedUser: User = this.userService.updateUser(user, changeCredentials)

    return ResponseEntity.status(HttpStatus.OK).body(savedUser)
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
  public fun deleteUser(@PathVariable id: Long): ResponseEntity<User>
  {
    this.userService.deleteUser(id)
    
    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
