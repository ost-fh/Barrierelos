package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.InvalidCredentialsException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.throwIfNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    throwIfNoValidCredentials(credential)

    val existingCredential = this.credentialRepository.findByUserFk(credential.userId).throwIfNull(NoSuchElementException()).toModel()

    throwIfUserIdChanged(credential, existingCredential)

    encodePasswordIfExists(credential)

    credential.id = existingCredential.id
    credential.modified = System.currentTimeMillis()

    return this.credentialRepository.save(credential.toEntity()).toModel()
  }

  public fun getCredential(userId: Long): Credential
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    return this.credentialRepository.findByUserFk(userId).throwIfNull(NoSuchElementException()).toModel()
  }

  @Transactional
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
    if((credential.password == null || credential.password == "") && (credential.issuer == null || credential.subject == null || credential.issuer == "" || credential.subject == ""))
    {
      throw InvalidCredentialsException("No password or oAuth issuer and subject provided.")
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
