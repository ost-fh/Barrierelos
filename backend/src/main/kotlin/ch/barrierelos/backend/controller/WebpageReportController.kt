package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.WEBPAGE_REPORT
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.message.WebpageReportsMessage
import ch.barrierelos.backend.model.WebpageReport
import ch.barrierelos.backend.service.WebpageReportService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebpageReportController
{
  @Autowired
  private lateinit var webpageReportService: WebpageReportService

  @PostMapping(value = [WEBPAGE_REPORT], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebpageReport(@RequestBody webpageReport: WebpageReport): ResponseEntity<WebpageReport>
  {
    webpageReport.id = 0

    this.webpageReportService.addWebpageReport(webpageReport)

    return ResponseEntity.status(HttpStatus.CREATED).body(webpageReport)
  }

  @GetMapping(value = [WEBPAGE_REPORT], produces = [MediaType.JSON])
  public fun getWebpageReports(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebpageReport>>
  {
    val webpageReportsPage = this.webpageReportService.getWebpageReports(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(webpageReportsPage.toHeaders()).body(webpageReportsPage.content)
  }

  @GetMapping(value = [WEBPAGE_REPORT], params = ["webpageId"], produces = [MediaType.JSON])
  public fun getWebpageReportsByWebpage(@RequestParam webpageId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebpageReport>>
  {
    val webpageReportsPage = this.webpageReportService.getWebpageReportsByWebpage(webpageId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(webpageReportsPage.toHeaders()).body(webpageReportsPage.content)
  }

  @GetMapping(value = ["$WEBPAGE_REPORT/for/{userId}"], produces = [MediaType.JSON])
  public fun getWebpageReportsForUser(@PathVariable userId: Long): ResponseEntity<WebpageReportsMessage>
  {
    val webpageReports = this.webpageReportService.getWebpageReportsForUser(userId)

    return ResponseEntity.status(HttpStatus.OK).body(webpageReports)
  }

  @GetMapping(value = ["$WEBPAGE_REPORT/{id}"], produces = [MediaType.JSON])
  public fun getWebpageReport(@PathVariable id: Long): ResponseEntity<WebpageReport>
  {
    val webpageReport: WebpageReport = this.webpageReportService.getWebpageReport(id)

    return ResponseEntity.status(HttpStatus.OK).body(webpageReport)
  }

  @DeleteMapping(value = ["$WEBPAGE_REPORT/{id}"], produces = [MediaType.JSON])
  public fun deleteWebpageReport(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.webpageReportService.deleteWebpageReport(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
