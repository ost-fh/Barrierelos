package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.WEBSITE_REPORT
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.message.WebsiteReportsMessage
import ch.barrierelos.backend.model.WebsiteReport
import ch.barrierelos.backend.service.WebsiteReportService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebsiteReportController
{
  @Autowired
  private lateinit var websiteReportService: WebsiteReportService

  @PostMapping(value = [WEBSITE_REPORT], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsiteReport(@RequestBody websiteReport: WebsiteReport): ResponseEntity<WebsiteReport>
  {
    websiteReport.id = 0

    this.websiteReportService.addWebsiteReport(websiteReport)

    return ResponseEntity.status(HttpStatus.CREATED).body(websiteReport)
  }

  @GetMapping(value = [WEBSITE_REPORT], produces = [MediaType.JSON])
  public fun getWebsiteReports(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebsiteReport>>
  {
    val websiteReportsPage = this.websiteReportService.getWebsiteReports(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websiteReportsPage.toHeaders()).body(websiteReportsPage.content)
  }

  @GetMapping(value = [WEBSITE_REPORT], params = ["websiteId"], produces = [MediaType.JSON])
  public fun getWebsiteReportsByWebsite(@RequestParam websiteId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebsiteReport>>
  {
    val websiteReportsPage = this.websiteReportService.getWebsiteReportsByWebsite(websiteId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websiteReportsPage.toHeaders()).body(websiteReportsPage.content)
  }

  @GetMapping(value = ["$WEBSITE_REPORT/for/{userId}"], produces = [MediaType.JSON])
  public fun getWebsiteReportsForUser(@PathVariable userId: Long): ResponseEntity<WebsiteReportsMessage>
  {
    val websiteReports = this.websiteReportService.getWebsiteReportsForUser(userId)

    return ResponseEntity.status(HttpStatus.OK).body(websiteReports)
  }

  @GetMapping(value = ["$WEBSITE_REPORT/{id}"], produces = [MediaType.JSON])
  public fun getWebsiteReport(@PathVariable id: Long): ResponseEntity<WebsiteReport>
  {
    val websiteReport: WebsiteReport = this.websiteReportService.getWebsiteReport(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteReport)
  }

  @DeleteMapping(value = ["$WEBSITE_REPORT/{id}"], produces = [MediaType.JSON])
  public fun deleteWebsiteReport(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.websiteReportService.deleteWebsiteReport(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
