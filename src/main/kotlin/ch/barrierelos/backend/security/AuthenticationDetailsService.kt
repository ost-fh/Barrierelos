package ch.barrierelos.backend.security

import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.enums.RoleEnum
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
public class AuthenticationDetailsService : UserDetailsService
{
  @Autowired
  private lateinit var authenticationService: AuthenticationService

  override fun loadUserByUsername(username: String): UserDetails
  {
    val user: User? = this.authenticationService.findUser(username)

    return if(user == null)
    {
      throw UsernameNotFoundException("User not found.")
    }
    else
    {
      val roles: Set<RoleEnum> = this.authenticationService.getRoles(user)
      val authorities: MutableCollection<GrantedAuthority> = this.authenticationService.getAuthorities(roles)

      AuthenticationDetails(user, roles, authorities)
    }
  }
}
