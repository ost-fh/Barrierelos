package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.STATISTICS
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.service.StatisticService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class StatisticController
{
  @Autowired
  private lateinit var statisticService: StatisticService

  @GetMapping(value = ["${STATISTICS}/{id}"], produces = [MediaType.JSON])
  public fun getWebsiteStatistic(@PathVariable id: Long): ResponseEntity<WebsiteDetails>
  {
    val websiteDetails = statisticService.getWebsiteDetails(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteDetails)
  }
}
