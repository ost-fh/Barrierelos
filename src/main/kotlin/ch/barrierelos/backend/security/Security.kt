package ch.barrierelos.backend.security

import ch.barrierelos.backend.model.enums.RoleEnum
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException

public object Security
{
  public fun getUserId(): Long
  {
    val principal = SecurityContextHolder.getContext().authentication.principal
    
    if(principal is AuthenticationDetails)
    {
      return principal.getUserId()
    }
    else
    {
      throw exception()
    }
  }
  
  public fun getUsername(): String
  {
    return SecurityContextHolder.getContext().authentication.name
  }
  
  public fun getRoles(): Set<RoleEnum>
  {
    val principal = SecurityContextHolder.getContext().authentication.principal

    if(principal is AuthenticationDetails)
    {
      return principal.getRoles()
    }
    else
    {
      throw exception()
    }
  }
  
  public fun hasId(id: Long): Boolean
  {
    return getUserId() == id
  }
  
  public fun hasRole(role: RoleEnum): Boolean
  {
    return getRoles().contains(role)
  }
  
  public fun hasAllRoles(vararg roles: RoleEnum): Boolean
  {
    return getRoles().containsAll(roles.asList())
  }
  
  public fun hasAnyRoles(vararg roles: RoleEnum): Boolean
  {
    return roles.any { it in getRoles() }
  }
  
  public fun hasRoleOrId(id: Long, role: RoleEnum): Boolean
  {
    return hasRole(role) || hasId(id)
  }
  
  public fun hasRoleAndId(id: Long, role: RoleEnum): Boolean
  {
    return hasRole(role) && hasId(id)
  }
  
  public fun hasAllRolesOrId(id: Long, vararg roles: RoleEnum): Boolean
  {
    return hasAllRoles(*roles) || hasId(id)
  }
  
  public fun hasAllRolesAndId(id: Long, vararg roles: RoleEnum): Boolean
  {
    return hasAllRoles(*roles) && hasId(id)
  }
  
  public fun hasAnyRolesOrId(id: Long, vararg roles: RoleEnum): Boolean
  {
    return hasAnyRoles(*roles) || hasId(id)
  }
  
  public fun hasAnyRolesAndId(id: Long, vararg roles: RoleEnum): Boolean
  {
    return hasAnyRoles(*roles) && hasId(id)
  }
  
  public fun assertId(id: Long)
  {
    if(!hasId(id)) throw exception()
  }
  
  public fun assertRole(role: RoleEnum)
  {
    if(!hasRole(role)) throw exception()
  }
  
  public fun assertAllRoles(vararg roles: RoleEnum)
  {
    if(!hasAllRoles(*roles)) throw exception()
  }
  
  public fun assertAnyRoles(vararg roles: RoleEnum)
  {
    if(!hasAnyRoles(*roles)) throw exception()
  }
  
  public fun assertRoleOrId(id: Long, role: RoleEnum)
  {
    if(!hasRoleOrId(id, role)) throw exception()
  }
  
  public fun assertRoleAndId(id: Long, role: RoleEnum)
  {
    if(!hasRoleAndId(id, role)) throw exception()
  }
  
  public fun assertAllRolesOrId(id: Long, vararg roles: RoleEnum)
  {
    if(!hasAllRolesOrId(id, *roles)) throw exception()
  }
  
  public fun assertAllRolesAndId(id: Long, vararg roles: RoleEnum)
  {
    if(!hasAllRolesAndId(id, *roles)) throw exception()
  }
  
  public fun assertAnyRolesOrId(id: Long, vararg roles: RoleEnum)
  {
    if(!hasAnyRolesOrId(id, *roles)) throw exception()
  }
  
  public fun assertAnyRolesAndId(id: Long, vararg roles: RoleEnum)
  {
    if(!hasAnyRolesAndId(id, *roles)) throw exception()
  }
  
  private fun exception(): ResponseStatusException
  {
    throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
  }
}
