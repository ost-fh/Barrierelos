package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.REPORT_MESSAGE
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.service.ReportMessageService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class ReportMessageController
{
  @Autowired
  private lateinit var reportMessageService: ReportMessageService

  @PostMapping(value = [REPORT_MESSAGE], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addReportMessage(@RequestBody reportMessage: ReportMessage): ResponseEntity<ReportMessage>
  {
    reportMessage.id = 0

    this.reportMessageService.addReportMessage(reportMessage)

    return ResponseEntity.status(HttpStatus.CREATED).body(reportMessage)
  }

  @PutMapping(value = ["$REPORT_MESSAGE/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateReportMessage(@PathVariable id: Long, @RequestBody reportMessage: ReportMessage): ResponseEntity<ReportMessage>
  {
    reportMessage.id = id

    this.reportMessageService.updateReportMessage(reportMessage)

    return ResponseEntity.status(HttpStatus.OK).body(reportMessage)
  }

  @GetMapping(value = [REPORT_MESSAGE], produces = [MediaType.JSON])
  public fun getReportMessages(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<ReportMessage>>
  {
    val reportMessagesPage = this.reportMessageService.getReportMessages(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(reportMessagesPage.toHeaders()).body(reportMessagesPage.content)
  }

  @GetMapping(value = [REPORT_MESSAGE], params = ["reportId"], produces = [MediaType.JSON])
  public fun getReportMessagesByReport(@RequestParam reportId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<ReportMessage>>
  {
    val reportMessagesPage = this.reportMessageService.getReportMessagesByReport(reportId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(reportMessagesPage.toHeaders()).body(reportMessagesPage.content)
  }

  @GetMapping(value = [REPORT_MESSAGE], params = ["userId"], produces = [MediaType.JSON])
  public fun getReportMessagesByUser(@RequestParam userId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<ReportMessage>>
  {
    val reportMessagesPage = this.reportMessageService.getReportMessagesByUser(userId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(reportMessagesPage.toHeaders()).body(reportMessagesPage.content)
  }

  @GetMapping(value = ["$REPORT_MESSAGE/{id}"], produces = [MediaType.JSON])
  public fun getReportMessage(@PathVariable id: Long): ResponseEntity<ReportMessage>
  {
    val reportMessage: ReportMessage = this.reportMessageService.getReportMessage(id)

    return ResponseEntity.status(HttpStatus.OK).body(reportMessage)
  }

  @DeleteMapping(value = ["$REPORT_MESSAGE/{id}"], produces = [MediaType.JSON])
  public fun deleteReportMessage(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.reportMessageService.deleteReportMessage(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
