package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.message.WebpageReportsMessage
import ch.barrierelos.backend.model.WebpageReport
import ch.barrierelos.backend.repository.ReportMessageRepository
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.repository.WebpageReportRepository
import ch.barrierelos.backend.repository.WebpageReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.WebpageReportRepository.Companion.findAllByWebpageFk
import ch.barrierelos.backend.repository.WebpageRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageReportService
{
  @Autowired
  private lateinit var webpageReportRepository: WebpageReportRepository
  @Autowired
  private lateinit var reportMessageRepository: ReportMessageRepository
  @Autowired
  private lateinit var userRepository: UserRepository
  @Autowired
  private lateinit var webpageRepository: WebpageRepository

  public fun addWebpageReport(webpageReport: WebpageReport): WebpageReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val timestamp = System.currentTimeMillis()
    webpageReport.report.created = timestamp
    webpageReport.report.modified = timestamp

    return this.webpageReportRepository.save(webpageReport.toEntity()).toModel(webpageReport)
  }

  public fun getWebpageReports(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.webpageReportRepository.findAll(defaultParameters)
  }

  public fun getWebpageReportsByWebpage(webpageId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageReport>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.webpageReportRepository.findAllByWebpageFk(webpageId, defaultParameters)
  }

  public fun getWebpageReportsForUser(userId: Long): WebpageReportsMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    // All report ids from messages written by the user
    val reportIds = this.reportMessageRepository.findAllReportIdsFromMessagesWrittenByTheUser(userId)
    // All of those reports that are webpage reports (filter out non-webpage reports)
    val reports = this.webpageReportRepository.findAllByReportFks(reportIds).toModels()
    // All messages from conversations concerning those reports
    val messages = this.reportMessageRepository.findAllByReportFkIn(reports.map { it.report.id }).toModels()
    // All user ids from those messages
    val userIds = messages.map { it.userId }.toSet()
    // All users for those ids
    val users = this.userRepository.findAllByUserIdIn(userIds).toModels()
    // All webpages for those reports
    val webpages = this.webpageRepository.findAllByWebpageIdIn(reports.map { it.webpageId }).toModels()

    return WebpageReportsMessage(
      reports = reports,
      messages = messages,
      users = users,
      webpages = webpages
    )
  }

  public fun getWebpageReport(webpageReportId: Long): WebpageReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.webpageReportRepository.findById(webpageReportId).orElseThrow().toModel()
  }

  public fun deleteWebpageReport(webpageReportId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.webpageReportRepository.deleteById(webpageReportId)
  }
}
