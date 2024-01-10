package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.message.UserReportsMessage
import ch.barrierelos.backend.model.UserReport
import ch.barrierelos.backend.repository.ReportMessageRepository
import ch.barrierelos.backend.repository.UserReportRepository
import ch.barrierelos.backend.repository.UserReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.UserReportRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class UserReportService
{
  @Autowired
  private lateinit var userReportRepository: UserReportRepository
  @Autowired
  private lateinit var reportMessageRepository: ReportMessageRepository
  @Autowired
  private lateinit var userRepository: UserRepository

  public fun addUserReport(userReport: UserReport): UserReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val timestamp = System.currentTimeMillis()
    userReport.report.created = timestamp
    userReport.report.modified = timestamp

    return this.userReportRepository.save(userReport.toEntity()).toModel(userReport)
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

  public fun getUserReportsForUser(userId: Long): UserReportsMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    // All report ids from messages written by the user
    val reportIds = this.reportMessageRepository.findAllReportIdsFromMessagesWrittenByTheUser(userId)
    // All of those reports that are user reports (filter out non-user reports)
    val reports = this.userReportRepository.findAllByReportFks(reportIds).toModels()
    // All messages from conversations concerning those reports
    val messages = this.reportMessageRepository.findAllByReportFkIn(reports.map { it.report.id }).toModels()
    // All user ids from both the reports and messages
    val userIds = mutableSetOf<Long>()
    userIds.addAll(messages.map { it.userId })
    userIds.addAll(reports.map { it.userId })
    // All users for those ids
    val users = this.userRepository.findAllByUserIdIn(userIds).toModels()

    return UserReportsMessage(
      reports = reports,
      messages = messages,
      users = users
    )
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
