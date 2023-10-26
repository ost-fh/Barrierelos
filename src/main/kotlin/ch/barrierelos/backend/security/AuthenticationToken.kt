package ch.barrierelos.backend.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

public class AuthenticationToken(private val jwt: Jwt, public val user: AuthenticationDetails, authorities: Collection<GrantedAuthority>) : AbstractAuthenticationToken(authorities)
{
  override fun getCredentials(): Jwt = jwt

  override fun getPrincipal(): AuthenticationDetails = user

  override fun isAuthenticated(): Boolean = true
}
