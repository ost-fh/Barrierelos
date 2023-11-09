package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.ANALYSIS
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.service.AnalysisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
public class AnalysisController
{
  @Autowired
  private lateinit var analysisService: AnalysisService

  @PostMapping(value = [ANALYSIS], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsite(@RequestBody website: WebsiteMessage): ResponseEntity<Any>
  {
    this.analysisService.scanWebsite(website)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
