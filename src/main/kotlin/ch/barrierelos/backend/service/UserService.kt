package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.InvalidCredentialsException
import ch.barrierelos.backend.exceptions.InvalidEmailException
import ch.barrierelos.backend.exceptions.NoRoleException
import ch.barrierelos.backend.exceptions.UserAlreadyExistsException
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.throwIfNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
public class UserService
{
  @Autowired
  private lateinit var userRepository: UserRepository
  @Autowired
  private lateinit var passwordEncoder: PasswordEncoder

  public fun addUser(user: User): User
  {
    if(user.roles.contains(RoleEnum.ADMIN)) Security.assertRole(RoleEnum.ADMIN)
    if(user.roles.contains(RoleEnum.MODERATOR)) Security.assertRole(RoleEnum.ADMIN)

    throwIfNoRole(user)
    throwIfNoValidCredentials(user)
    throwIfNoValidEmail(user)
    throwIfUsernameAlreadyExists(user)
    encodePasswordIfExists(user)

    user.modified = System.currentTimeMillis()

    return this.userRepository.save(user.toEntity()).toModel().preventExposingCredentials()
  }

  public fun updateUser(user: User, changeCredentials: Boolean): User
  {
    Security.assertRoleOrId(user.id, RoleEnum.ADMIN)

    if(user.roles.contains(RoleEnum.ADMIN)) Security.assertRole(RoleEnum.ADMIN)
    if(user.roles.contains(RoleEnum.MODERATOR)) Security.assertRole(RoleEnum.ADMIN)

    val existingUser = this.userRepository.findByUserId(user.id).throwIfNull(NoSuchElementException()).toModel()

    throwIfNoRole(user)
    throwIfNoValidEmail(user)
    if(user.username != existingUser.username) throwIfUsernameAlreadyExists(user)

    user.modified = System.currentTimeMillis()

    if(changeCredentials)
    {
      throwIfNoValidCredentials(user)
      encodePasswordIfExists(user)
    }
    else
    {
      // Prevent changing credentials
      user.password = existingUser.password
      user.issuer = existingUser.issuer
      user.subject = existingUser.subject
    }

    return this.userRepository.save(user.toEntity()).toModel().preventExposingCredentials()
  }

  public fun getUsers(defaultParameters: DefaultParameters = DefaultParameters()): Result<User>
  {
    Security.assertRole(RoleEnum.ADMIN)

    return this.userRepository.findAll(defaultParameters, UserEntity::class.java, UserEntity::toModel)
      .also { userPage -> userPage.content.forEach { user -> user.preventExposingCredentials() } }
  }

  public fun getUser(userId: Long): User
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    return this.userRepository.findById(userId).orElseThrow().toModel().preventExposingCredentials()
  }

  public fun deleteUser(userId: Long)
  {
    Security.assertRoleOrId(userId, RoleEnum.ADMIN)

    this.userRepository.checkIfExists(userId)

    this.userRepository.deleteById(userId)
  }

  private fun throwIfNoRole(user: User)
  {
    if(user.roles.isEmpty())
    {
      throw NoRoleException("No role provided.")
    }
  }

  private fun throwIfNoValidCredentials(user: User)
  {
    if((user.password == null || user.password == "") && (user.issuer == null || user.subject == null || user.issuer == "" || user.subject == ""))
    {
      throw InvalidCredentialsException("No password or oAuth issuer and subject provided.")
    }
  }

  private fun throwIfUsernameAlreadyExists(user: User)
  {
    if(this.userRepository.findByUsername(user.username) != null)
    {
      throw UserAlreadyExistsException("User with that username already exists.")
    }
  }

  private fun throwIfNoValidEmail(user: User)
  {
    if(!user.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()))
    {
      throw InvalidEmailException("Email address is not valid.")
    }
  }

  private fun encodePasswordIfExists(user: User)
  {
    if(user.password != null)
    {
      // Encode the password
      user.password = this.passwordEncoder.encode(user.password)
    }
  }

  private fun User.preventExposingCredentials(): User
  {
    this.password = null
    this.issuer = null
    this.subject = null

    return this
  }
}
