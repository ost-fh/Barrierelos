package ch.barrierelos.backend.security

import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User
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
      val authorities: MutableCollection<GrantedAuthority> = this.authenticationService.getAuthorities(user.roles)
      val credential: Credential? = this.authenticationService.getCredential(user)

      if(credential == null)
      {
        throw NoAuthorizationException()
      }
      else
      {
        AuthenticationDetails(user, credential, authorities)
      }
    }
  }
}
