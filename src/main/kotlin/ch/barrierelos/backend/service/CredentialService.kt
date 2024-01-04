package ch.barrierelos.backend.service

import ch.barrierelos.backend.constants.Credentials.ALLOWED_ISSUER
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exception.InvalidCredentialsException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.isValidPassword
import ch.barrierelos.backend.util.throwIfNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
public class CredentialService
{
  @Autowired
  private lateinit var credentialRepository: CredentialRepository
  @Autowired
  private lateinit var passwordEncoder: PasswordEncoder

  public fun addCredential(credential: Credential): Credential
  {
    throwIfNoValidCredentials(credential)

    encodePasswordIfExists(credential)

    val timestamp = System.currentTimeMillis()
    credential.created = timestamp
    credential.modified = timestamp

    return this.credentialRepository.save(credential.toEntity()).toModel(credential)
  }

  public fun updateCredential(credential: Credential): Credential
  {
    Security.assertRoleOrId(credential.userId, RoleEnum.ADMIN)

    val existingCredential = this.credentialRepository.findByUserFk(credential.userId).throwIfNull(NoSuchElementException()).toModel()

    throwIfNoValidCredentials(credential)
    throwIfUserIdChanged(credential, existingCredential)

    encodePasswordIfExists(credential)

    credential.id = existingCredential.id
    credential.modified = System.currentTimeMillis()
    credential.created = existingCredential.created

    return this.credentialRepository.save(credential.toEntity()).toModel()
  }

  public fun getCredential(userId: Long): Credential
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    return this.credentialRepository.findByUserFk(userId).throwIfNull(NoSuchElementException()).toModel()
  }

  public fun deleteCredential(userId: Long)
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    throwIfNotExists(userId)

    this.credentialRepository.deleteByUserFk(userId)
  }

  public fun throwIfNotExists(userId: Long)
  {
    if(!this.credentialRepository.existsByUserFk(userId)) throw NoSuchElementException()
  }

  public fun throwIfNoValidCredentials(credential: Credential)
  {
    if(credential.password != null && credential.password != "" && credential.password!!.isValidPassword() && credential.issuer == null && credential.subject == null)
    {
      return
    }
    else if(credential.password == null && credential.issuer != null && credential.subject != null && ALLOWED_ISSUER.contains(credential.issuer))
    {
      return
    }
    else
    {
      throw InvalidCredentialsException("No valid credentials provided.")
    }
  }

  private fun throwIfUserIdChanged(credential: Credential, existingCredential: Credential)
  {
    if(credential.userId != existingCredential.userId) throw NoAuthorizationException()
  }

  private fun encodePasswordIfExists(credential: Credential)
  {
    if(credential.password != null)
    {
      // Encode the password
      credential.password = this.passwordEncoder.encode(credential.password)
    }
  }
}
