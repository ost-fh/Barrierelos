package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.STATISTICS
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.service.StatisticService
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
  private lateinit var statisticService: StatisticService

  @GetMapping(value = ["${STATISTICS}/{id}"], produces = [MediaType.JSON])
  public fun getWebsiteScan(@PathVariable id: Long): ResponseEntity<WebsiteScanMessage>
  {
    val websiteScan = statisticService.getWebsiteScan(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteScan)
  }
}
