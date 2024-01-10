package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.message.WebsiteReportsMessage
import ch.barrierelos.backend.model.WebsiteReport
import ch.barrierelos.backend.repository.ReportMessageRepository
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.repository.WebsiteReportRepository
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAllByWebsiteFk
import ch.barrierelos.backend.repository.WebsiteRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebsiteReportService
{
  @Autowired
  private lateinit var websiteReportRepository: WebsiteReportRepository
  @Autowired
  private lateinit var reportMessageRepository: ReportMessageRepository
  @Autowired
  private lateinit var userRepository: UserRepository
  @Autowired
  private lateinit var websiteRepository: WebsiteRepository

  public fun addWebsiteReport(websiteReport: WebsiteReport): WebsiteReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val timestamp = System.currentTimeMillis()
    websiteReport.report.created = timestamp
    websiteReport.report.modified = timestamp

    return this.websiteReportRepository.save(websiteReport.toEntity()).toModel(websiteReport)
  }

  public fun getWebsiteReports(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.websiteReportRepository.findAll(defaultParameters)
  }

  public fun getWebsiteReportsByWebsite(websiteId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.websiteReportRepository.findAllByWebsiteFk(websiteId, defaultParameters)
  }

  public fun getWebsiteReportsForUser(userId: Long): WebsiteReportsMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    // All report ids from messages written by the user
    val reportIds = this.reportMessageRepository.findAllReportIdsFromMessagesWrittenByTheUser(userId)
    // All of those reports that are website reports (filter out non-website reports)
    val reports = this.websiteReportRepository.findAllByReportFks(reportIds).toModels()
    // All messages from conversations concerning those reports
    val messages = this.reportMessageRepository.findAllByReportFkIn(reports.map { it.report.id }).toModels()
    // All user ids from those messages
    val userIds = messages.map { it.userId }.toSet()
    // All users for those ids
    val users = this.userRepository.findAllByUserIdIn(userIds).toModels()
    // All websites for those reports
    val websites = this.websiteRepository.findAllByWebsiteIdIn(reports.map { it.websiteId }).toModels()

    return WebsiteReportsMessage(
      reports = reports,
      messages = messages,
      users = users,
      websites = websites
    )
  }

  public fun getWebsiteReport(websiteReportId: Long): WebsiteReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.websiteReportRepository.findById(websiteReportId).orElseThrow().toModel()
  }

  public fun deleteWebsiteReport(websiteReportId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.websiteReportRepository.deleteById(websiteReportId)
  }
}
