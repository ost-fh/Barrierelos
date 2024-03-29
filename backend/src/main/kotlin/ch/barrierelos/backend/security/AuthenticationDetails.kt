package ch.barrierelos.backend.security

import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

public class AuthenticationDetails public constructor(private val user: User, private val credential: Credential, private val authorities: MutableCollection<GrantedAuthority>) : UserDetails
{
  public fun getUser(): User = this.user

  public fun getUserId(): Long = this.user.id

  public fun getRoles(): Set<RoleEnum> = this.user.roles
  
  override fun getAuthorities(): MutableCollection<GrantedAuthority> = this.authorities
  
  override fun getPassword(): String = this.credential.password ?: ""

  override fun getUsername(): String = this.user.username
  
  override fun isAccountNonExpired(): Boolean = true
  
  override fun isAccountNonLocked(): Boolean = true
  
  override fun isCredentialsNonExpired(): Boolean = true
  
  override fun isEnabled(): Boolean = true
}
