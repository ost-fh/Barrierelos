package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.UserReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.UserReportRepository
import ch.barrierelos.backend.repository.UserReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.UserReportRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class UserReportService
{
  @Autowired
  private lateinit var userReportRepository: UserReportRepository

  public fun addUserReport(userReport: UserReport): UserReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.userReportRepository.save(userReport.toEntity()).toModel()
  }

  public fun getUserReports(defaultParameters: DefaultParameters = DefaultParameters()): Result<UserReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.userReportRepository.findAll(defaultParameters)
  }

  public fun getUserReportsByUser(userId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<UserReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.userReportRepository.findAllByUserFk(userId, defaultParameters)
  }

  public fun getUserReport(userReportId: Long): UserReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.userReportRepository.findById(userReportId).orElseThrow().toModel()
  }

  public fun deleteUserReport(userReportId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.userReportRepository.deleteById(userReportId)
  }
}
