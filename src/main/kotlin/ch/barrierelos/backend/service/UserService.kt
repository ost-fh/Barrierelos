package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.enums.RoleEnum
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
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
    Security.assertAnyRoles(RoleEnum.ADMIN)

    user.modified = System.currentTimeMillis()

    // Encode the password
    user.password = this.passwordEncoder.encode(user.password)

    // Prevent exposing the password
    return this.userRepository.save(user.toEntity()).toModel()
      .also { it.password = "" }
  }

  public fun updateUser(user: User, changePassword: Boolean = false): User
  {
    Security.assertAnyRolesOrId(user.id, RoleEnum.ADMIN)

    this.userRepository.checkIfExists(user.id)

    user.modified = System.currentTimeMillis()

    if(changePassword)
    {
      // Encode the password
      user.password = this.passwordEncoder.encode(user.password)
    }
    else
    {
      // Prevent changing the password
      user.password = this.userRepository.getPasswordById(user.id)
    }

    // Prevent exposing the password
    return this.userRepository.save(user.toEntity()).toModel()
      .also { it.password = "" }
  }

  public fun getUsers(defaultParameters: DefaultParameters): Result<User>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    // Prevent exposing the password
    return this.userRepository.findAll(defaultParameters, UserEntity::class.java, UserEntity::toModel)
      .also { userPage -> userPage.content.forEach { it.password = "" } }
  }

  public fun getUser(userId: Long): User
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    // Prevent exposing the password
    return this.userRepository.findById(userId).orElseThrow().toModel()
      .also { it.password = "" }
  }

  public fun deleteUser(userId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.userRepository.checkIfExists(userId)

    this.userRepository.deleteById(userId)
  }
}
