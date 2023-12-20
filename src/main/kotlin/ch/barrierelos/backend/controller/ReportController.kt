package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.REPORT
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.Report
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.ReportService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class ReportController
{
  @Autowired
  private lateinit var reportService: ReportService

  @PostMapping(value = [REPORT], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addReport(@RequestBody report: Report): ResponseEntity<Report>
  {
    report.id = 0

    this.reportService.addReport(report)

    return ResponseEntity.status(HttpStatus.CREATED).body(report)
  }

  @PutMapping(value = ["$REPORT/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateReport(@PathVariable id: Long, @RequestBody report: Report): ResponseEntity<Report>
  {
    report.id = id

    this.reportService.updateReport(report)

    return ResponseEntity.status(HttpStatus.OK).body(report)
  }

  @GetMapping(value = [REPORT], produces = [MediaType.JSON])
  public fun getReports(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<Report>>
  {
    val reportsPage = this.reportService.getReports(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(reportsPage.toHeaders()).body(reportsPage.content)
  }

  @GetMapping(value = [REPORT], params = ["userId"], produces = [MediaType.JSON])
  public fun getReports(@RequestParam userId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<Report>>
  {
    val reportsPage = this.reportService.getReportsByUser(userId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(reportsPage.toHeaders()).body(reportsPage.content)
  }

  @GetMapping(value = ["$REPORT/{id}"], produces = [MediaType.JSON])
  public fun getReport(@PathVariable id: Long): ResponseEntity<Report>
  {
    val report: Report = this.reportService.getReport(id)

    return ResponseEntity.status(HttpStatus.OK).body(report)
  }

  @DeleteMapping(value = ["$REPORT/{id}"], produces = [MediaType.JSON])
  public fun deleteReport(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.reportService.deleteReport(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
