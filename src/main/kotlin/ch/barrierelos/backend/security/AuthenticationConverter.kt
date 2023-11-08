package ch.barrierelos.backend.security

import ch.barrierelos.backend.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
public class AuthenticationConverter : Converter<Jwt, AuthenticationToken>
{
  @Autowired
  private lateinit var authenticationService: AuthenticationService

  override fun convert(jwt: Jwt): AuthenticationToken
  {
    val user: User? = this.authenticationService.findUser(jwt.issuer.authority, jwt.subject)

    return if(user == null)
    {
      throw UsernameNotFoundException("User not found.")
    }
    else
    {
      val authorities: MutableCollection<GrantedAuthority> = this.authenticationService.getAuthorities(user.roles)
      val principal = AuthenticationDetails(user, authorities)

      AuthenticationToken(jwt, principal, authorities)
    }
  }
}
