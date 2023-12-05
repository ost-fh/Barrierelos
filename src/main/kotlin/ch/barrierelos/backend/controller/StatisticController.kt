package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.STATISTICS
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.service.DetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
public class StatisticController
{
  @Autowired
  private lateinit var statisticService: DetailService

  @GetMapping(value = ["${STATISTICS}/{id}"], produces = [MediaType.JSON])
  public fun getWebsiteDetails(@PathVariable id: Long): ResponseEntity<WebsiteDetails>
  {
    val websiteDetails = statisticService.getWebsiteDetails(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteDetails)
  }
}
