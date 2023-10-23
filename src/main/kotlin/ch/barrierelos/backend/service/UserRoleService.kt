package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.UserRoleEntity
import ch.barrierelos.backend.message.enums.Order
import ch.barrierelos.backend.model.UserRole
import ch.barrierelos.backend.model.enums.RoleEnum
import ch.barrierelos.backend.repository.Repository.Companion.checkIfExists
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.UserRoleRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class UserRoleService
{
  @Autowired
  private lateinit var userRoleRepository: UserRoleRepository

  public fun addUserRole(userRole: UserRole): UserRole
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    userRole.modified = System.currentTimeMillis()

    return this.userRoleRepository.save(userRole.toEntity()).toModel()
  }

  public fun updateUserRole(userRole: UserRole): UserRole
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.userRoleRepository.checkIfExists(userRole.id)

    userRole.modified = System.currentTimeMillis()

    return this.userRoleRepository.save(userRole.toEntity()).toModel()
  }

  public fun getUserRoles(page: Int?, size: Int?, sort: String?, order: Order?, modifiedAfter: Long?): Result<UserRole>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    return this.userRoleRepository.findAll(page, size, sort, order, modifiedAfter, UserRoleEntity::class.java, UserRoleEntity::toModel)
  }

  public fun getUserRole(userRoleId: Long): UserRole
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    return this.userRoleRepository.findById(userRoleId).orElseThrow().toModel()
  }

  public fun deleteUserRole(userRoleId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.userRoleRepository.checkIfExists(userRoleId)

    this.userRoleRepository.deleteById(userRoleId)
  }
}
