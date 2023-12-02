package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.InvalidEmailException
import ch.barrierelos.backend.exceptions.NoRoleException
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.orThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class UserService
{
  @Autowired
  private lateinit var credentialService: CredentialService
  @Autowired
  private lateinit var userRepository: UserRepository

  public fun addUser(user: User, credential: Credential): User
  {
    if(user.roles.contains(RoleEnum.ADMIN)) Security.assertRole(RoleEnum.ADMIN)
    if(user.roles.contains(RoleEnum.MODERATOR)) Security.assertRole(RoleEnum.ADMIN)

    throwIfNoRole(user)
    throwIfNoValidEmail(user)
    throwIfUsernameAlreadyExists(user)
    this.credentialService.throwIfNoValidCredentials(credential)

    val timestamp = System.currentTimeMillis()
    user.created = timestamp
    user.modified = timestamp

    this.userRepository.save(user.toEntity()).toModel(user)

    credential.userId = user.id

    this.credentialService.addCredential(credential)

    return user
  }

  public fun updateUser(user: User): User
  {
    Security.assertRoleOrId(user.id, RoleEnum.ADMIN)

    if(user.roles.contains(RoleEnum.ADMIN)) Security.assertRole(RoleEnum.ADMIN)
    if(user.roles.contains(RoleEnum.MODERATOR)) Security.assertRole(RoleEnum.ADMIN)

    throwIfNoRole(user)
    throwIfNoValidEmail(user)
    throwIfUsernameChangedAndAlreadyExists(user)

    user.modified = System.currentTimeMillis()

    return this.userRepository.save(user.toEntity()).toModel(user)
  }

  public fun getUsers(defaultParameters: DefaultParameters = DefaultParameters()): Result<User>
  {
    Security.assertRole(RoleEnum.ADMIN)

    return this.userRepository.findAll(defaultParameters, UserEntity::class.java, UserEntity::toModel)
  }

  public fun getUser(userId: Long): User
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    return this.userRepository.findById(userId).orThrow(NoSuchElementException()).toModel()
  }

  public fun deleteUser(userId: Long)
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    this.userRepository.checkIfExists(userId)

    this.credentialService.deleteCredential(userId)

    this.userRepository.deleteById(userId)
  }

  private fun throwIfNoRole(user: User)
  {
    if(user.roles.isEmpty())
    {
      throw NoRoleException("No role provided.")
    }
  }

  private fun throwIfUsernameAlreadyExists(user: User)
  {
    if(this.userRepository.findByUsername(user.username) != null)
    {
      throw AlreadyExistsException("User with that username already exists.")
    }
  }

  private fun throwIfUsernameChangedAndAlreadyExists(user: User)
  {
    val existingUser = this.userRepository.findById(user.id).orThrow(NoSuchElementException()).toModel()

    if(user.username != existingUser.username)
    {
      throwIfUsernameAlreadyExists(user)
    }
  }

  private fun throwIfNoValidEmail(user: User)
  {
    if(!user.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()))
    {
      throw InvalidEmailException("Email address is not valid.")
    }
  }
}
