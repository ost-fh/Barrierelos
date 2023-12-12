package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBSITE_STATISTIC
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebsiteStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteStatisticService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebsiteStatisticController
{
  @Autowired
  private lateinit var websiteStatisticService: WebsiteStatisticService

  @PostMapping(value = [WEBSITE_STATISTIC], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsiteStatistic(@RequestBody websiteStatistic: WebsiteStatistic): ResponseEntity<WebsiteStatistic>
  {
    websiteStatistic.id = 0

    this.websiteStatisticService.addWebsiteStatistic(websiteStatistic)

    return ResponseEntity.status(HttpStatus.CREATED).body(websiteStatistic)
  }

  @PutMapping(value = ["$WEBSITE_STATISTIC/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebsiteStatistic(@PathVariable id: Long, @RequestBody websiteStatistic: WebsiteStatistic): ResponseEntity<WebsiteStatistic>
  {
    websiteStatistic.id = id

    this.websiteStatisticService.updateWebsiteStatistic(websiteStatistic)

    return ResponseEntity.status(HttpStatus.OK).body(websiteStatistic)
  }

  @GetMapping(value = [WEBSITE_STATISTIC], produces = [MediaType.JSON])
  public fun getWebsiteStatistics(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebsiteStatistic>>
  {
    val websiteStatisticsPage = this.websiteStatisticService.getWebsiteStatistics(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websiteStatisticsPage.toHeaders()).body(websiteStatisticsPage.content)
  }

  @GetMapping(value = ["$WEBSITE_STATISTIC/{id}"], produces = [MediaType.JSON])
  public fun getWebsiteStatistic(@PathVariable id: Long): ResponseEntity<WebsiteStatistic>
  {
    val websiteStatistic: WebsiteStatistic = this.websiteStatisticService.getWebsiteStatistic(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteStatistic)
  }

  @DeleteMapping(value = ["$WEBSITE_STATISTIC/{id}"], produces = [MediaType.JSON])
  public fun deleteWebsiteStatistic(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.websiteStatisticService.deleteWebsiteStatistic(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
