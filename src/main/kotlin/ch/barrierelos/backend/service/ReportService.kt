package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.ReportEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.model.Report
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.ReportRepository
import ch.barrierelos.backend.repository.ReportRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class ReportService
{
  @Autowired
  private lateinit var reportRepository: ReportRepository

  public fun addReport(report: Report): Report
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val timestamp = System.currentTimeMillis()
    report.created = timestamp
    report.modified = timestamp

    report.state = StateEnum.OPEN

    return this.reportRepository.save(report.toEntity()).toModel(report)
  }

  public fun updateReport(report: Report): Report
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    val existingReport = this.reportRepository.findById(report.id).orElseThrow().toModel()
    
    if(!Security.hasRole(RoleEnum.ADMIN))
    {
      throwIfChangeOtherThanState(report, existingReport)
    }

    report.modified = System.currentTimeMillis()
    report.created = existingReport.created

    return this.reportRepository.save(report.toEntity()).toModel()
  }

  public fun getReports(defaultParameters: DefaultParameters = DefaultParameters()): Result<Report>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportRepository.findAll(defaultParameters, ReportEntity::class.java, ReportEntity::toModel)
  }

  public fun getReportsByUser(userId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<Report>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportRepository.findAllByUserFk(userId, defaultParameters)
  }

  public fun getReport(reportId: Long): Report
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportRepository.findById(reportId).orElseThrow().toModel()
  }

  public fun deleteReport(reportId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.reportRepository.deleteById(reportId)
  }

  public fun throwIfChangeOtherThanState(report: Report, existingReport: Report)
  {
    if((report.id != existingReport.id)
      || (report.userId != existingReport.userId)
      || (report.reason != existingReport.reason)
      || (report.created != existingReport.created))
    {
      throw IllegalArgumentException("Report illegally modified.")
    }
  }
}
