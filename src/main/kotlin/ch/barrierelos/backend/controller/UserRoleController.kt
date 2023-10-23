package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.USER_ROLE
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.util.toHeaders
import ch.barrierelos.backend.message.enums.Order
import ch.barrierelos.backend.model.UserRole
import ch.barrierelos.backend.service.UserRoleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class UserRoleController
{
  @Autowired
  private lateinit var userRoleService: UserRoleService
  
  @PostMapping(value = [USER_ROLE], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addUserRole(@RequestBody userRole: UserRole): ResponseEntity<UserRole>
  {
    userRole.id = 0
    
    val userRole: UserRole = this.userRoleService.addUserRole(userRole)
    
    return ResponseEntity.status(HttpStatus.CREATED).body(userRole)
  }
  
  @PutMapping(value = ["$USER_ROLE/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateUserRole(@PathVariable id: Long, @RequestBody userRole: UserRole): ResponseEntity<UserRole>
  {
    userRole.id = id
    
    val userRole: UserRole = this.userRoleService.updateUserRole(userRole)
    
    return ResponseEntity.status(HttpStatus.OK).body(userRole)
  }
  
  @GetMapping(value = [USER_ROLE], produces = [MediaType.JSON])
  public fun getUserRoles(@RequestParam page: Int?, @RequestParam size: Int?, @RequestParam sort: String?, @RequestParam order: Order?, @RequestParam modifiedAfter: Long?): ResponseEntity<List<UserRole>>
  {
    val userRoleResult = this.userRoleService.getUserRoles(page, size, sort, order, modifiedAfter)
    
    return ResponseEntity.status(HttpStatus.OK).headers(userRoleResult.toHeaders()).body(userRoleResult.content)
  }
  
  @GetMapping(value = ["$USER_ROLE/{id}"], produces = [MediaType.JSON])
  public fun getUserRole(@PathVariable id: Long): ResponseEntity<UserRole>
  {
    val userRole: UserRole = this.userRoleService.getUserRole(id)
    
    return ResponseEntity.status(HttpStatus.OK).body(userRole)
  }
  
  @DeleteMapping(value = ["$USER_ROLE/{id}"], produces = [MediaType.JSON])
  public fun deleteUserRole(@PathVariable id: Long): ResponseEntity<UserRole>
  {
    this.userRoleService.deleteUserRole(id)
    
    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
