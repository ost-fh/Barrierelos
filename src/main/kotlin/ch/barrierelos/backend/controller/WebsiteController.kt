package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBSITE
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.scanner.ScanJob
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteService
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebsiteController
{
  @Autowired
  private lateinit var websiteService: WebsiteService

  @PostMapping(value = [WEBSITE], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsite(@RequestBody websiteMessage: WebsiteMessage): ResponseEntity<Website>
  {
    val website = this.websiteService.addWebsite(websiteMessage)

    return ResponseEntity.status(HttpStatus.CREATED).body(website)
  }

  @PutMapping(value = ["${WEBSITE}/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebsite(@PathVariable id: Long, @RequestBody website: Website): ResponseEntity<Website>
  {
    website.id = id

    this.websiteService.updateWebsite(website)

    return ResponseEntity.status(HttpStatus.OK).body(website)
  }

  @GetMapping(value = [WEBSITE], produces = [MediaType.JSON])
  public fun getWebsites(showDeleted: Boolean = false, showBlocked: Boolean = false, @ParameterObject defaultParameters: DefaultParameters): ResponseEntity<Result<Website>>
  {
    val websitePage = this.websiteService.getWebsites(showDeleted, showBlocked, defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websitePage.toHeaders()).body(websitePage)
  }

  @GetMapping(value = ["${WEBSITE}/{id}"], produces = [MediaType.JSON])
  public fun getWebsite(@PathVariable id: Long): ResponseEntity<Website>
  {
    val website: Website = this.websiteService.getWebsite(id)

    return ResponseEntity.status(HttpStatus.OK).body(website)
  }

  @DeleteMapping(value = ["${WEBSITE}/{id}"], produces = [MediaType.JSON])
  public fun deleteWebsite(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.websiteService.deleteWebsite(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }

  @PostMapping(value = ["${WEBSITE}/{id}/scans"], produces = [MediaType.JSON])
  public fun scanWebsite(@PathVariable id: Long): ResponseEntity<ScanJob>
  {
    val scanJob = this.websiteService.scanWebsite(id)

    return ResponseEntity.status(HttpStatus.OK).body(scanJob)
  }
}
