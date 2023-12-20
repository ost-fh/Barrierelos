package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebsiteReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.WebsiteReportRepository
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAllByWebsiteFk
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebsiteReportService
{
  @Autowired
  private lateinit var websiteReportRepository: WebsiteReportRepository

  public fun addWebsiteReport(websiteReport: WebsiteReport): WebsiteReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.websiteReportRepository.save(websiteReport.toEntity()).toModel()
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
