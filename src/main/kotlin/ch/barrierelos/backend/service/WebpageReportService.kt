package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebpageReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.WebpageReportRepository
import ch.barrierelos.backend.repository.WebpageReportRepository.Companion.findAll
import ch.barrierelos.backend.repository.WebpageReportRepository.Companion.findAllByWebpageFk
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageReportService
{
  @Autowired
  private lateinit var webpageReportRepository: WebpageReportRepository

  public fun addWebpageReport(webpageReport: WebpageReport): WebpageReport
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.webpageReportRepository.save(webpageReport.toEntity()).toModel()
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
