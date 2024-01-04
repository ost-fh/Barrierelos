package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.USER_REPORT
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.message.UserReportsMessage
import ch.barrierelos.backend.model.UserReport
import ch.barrierelos.backend.service.UserReportService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class UserReportController
{
  @Autowired
  private lateinit var userReportService: UserReportService

  @PostMapping(value = [USER_REPORT], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addUserReport(@RequestBody userReport: UserReport): ResponseEntity<UserReport>
  {
    userReport.id = 0

    this.userReportService.addUserReport(userReport)

    return ResponseEntity.status(HttpStatus.CREATED).body(userReport)
  }

  @GetMapping(value = [USER_REPORT], produces = [MediaType.JSON])
  public fun getUserReports(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<UserReport>>
  {
    val userReportsPage = this.userReportService.getUserReports(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(userReportsPage.toHeaders()).body(userReportsPage.content)
  }

  @GetMapping(value = [USER_REPORT], params = ["userId"], produces = [MediaType.JSON])
  public fun getUserReportsByUser(@RequestParam userId: Long, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<UserReport>>
  {
    val userReportsPage = this.userReportService.getUserReportsByUser(userId, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(userReportsPage.toHeaders()).body(userReportsPage.content)
  }

  @GetMapping(value = ["$USER_REPORT/for/{userId}"], produces = [MediaType.JSON])
  public fun getUserReportsForUser(@PathVariable userId: Long): ResponseEntity<UserReportsMessage>
  {
    val userReports = this.userReportService.getUserReportsForUser(userId)

    return ResponseEntity.status(HttpStatus.OK).body(userReports)
  }

  @GetMapping(value = ["$USER_REPORT/{id}"], produces = [MediaType.JSON])
  public fun getUserReport(@PathVariable id: Long): ResponseEntity<UserReport>
  {
    val userReport: UserReport = this.userReportService.getUserReport(id)

    return ResponseEntity.status(HttpStatus.OK).body(userReport)
  }

  @DeleteMapping(value = ["$USER_REPORT/{id}"], produces = [MediaType.JSON])
  public fun deleteUserReport(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.userReportService.deleteUserReport(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
