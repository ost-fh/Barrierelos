package ch.barrierelos.backend.security

import ch.barrierelos.backend.model.enums.RoleEnum
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

public class AuthenticationDetails(private val userId: Long, private val username: String, private val password: String, roles: Collection<RoleEnum>) : UserDetails
{
  private val roles: MutableCollection<GrantedAuthority>

  init
  {
    this.roles = roles.map { role -> SimpleGrantedAuthority("ROLE_" + role.name) }.toMutableList()
  }
  
  public fun getUserId(): Long = userId
  
  override fun getAuthorities(): MutableCollection<GrantedAuthority> = roles
  
  override fun getPassword(): String = password
  
  override fun getUsername(): String = username
  
  override fun isAccountNonExpired(): Boolean = true
  
  override fun isAccountNonLocked(): Boolean = true
  
  override fun isCredentialsNonExpired(): Boolean = true
  
  override fun isEnabled(): Boolean = true
}
