package ch.barrierelos.backend.security

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
public class AuthenticationService
{
  @Autowired
  private lateinit var userRepository: UserRepository
  @Autowired
  private lateinit var credentialRepository: CredentialRepository

  public fun findUser(username: String): User? = this.userRepository.findByUsername(username)?.toModel()

  public fun findUser(issuer: String, subject: String): User? = this.userRepository.findByIssuerAndSubject(issuer, subject)?.toModel()

  public fun getCredential(user: User): Credential? = this.credentialRepository.findByUserFk(user.id)?.toModel()

  public fun getAuthorities(roles: Set<RoleEnum>): MutableSet<GrantedAuthority> = roles.map { role -> SimpleGrantedAuthority("ROLE_" + role.name) }.toMutableSet()
}
