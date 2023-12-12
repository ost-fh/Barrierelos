package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBPAGE_STATISTIC
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebpageStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageStatisticService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebpageStatisticController
{
  @Autowired
  private lateinit var webpageStatisticService: WebpageStatisticService

  @PostMapping(value = [WEBPAGE_STATISTIC], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebpageStatistic(@RequestBody webpageStatistic: WebpageStatistic): ResponseEntity<WebpageStatistic>
  {
    webpageStatistic.id = 0

    this.webpageStatisticService.addWebpageStatistic(webpageStatistic)

    return ResponseEntity.status(HttpStatus.CREATED).body(webpageStatistic)
  }

  @PutMapping(value = ["$WEBPAGE_STATISTIC/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebpageStatistic(@PathVariable id: Long, @RequestBody webpageStatistic: WebpageStatistic): ResponseEntity<WebpageStatistic>
  {
    webpageStatistic.id = id

    this.webpageStatisticService.updateWebpageStatistic(webpageStatistic)

    return ResponseEntity.status(HttpStatus.OK).body(webpageStatistic)
  }

  @GetMapping(value = [WEBPAGE_STATISTIC], produces = [MediaType.JSON])
  public fun getWebpageStatistics(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebpageStatistic>>
  {
    val webpageStatisticsPage = this.webpageStatisticService.getWebpageStatistics(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(webpageStatisticsPage.toHeaders()).body(webpageStatisticsPage.content)
  }

  @GetMapping(value = ["$WEBPAGE_STATISTIC/{id}"], produces = [MediaType.JSON])
  public fun getWebpageStatistic(@PathVariable id: Long): ResponseEntity<WebpageStatistic>
  {
    val webpageStatistic: WebpageStatistic = this.webpageStatisticService.getWebpageStatistic(id)

    return ResponseEntity.status(HttpStatus.OK).body(webpageStatistic)
  }

  @DeleteMapping(value = ["$WEBPAGE_STATISTIC/{id}"], produces = [MediaType.JSON])
  public fun deleteWebpageStatistic(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.webpageStatisticService.deleteWebpageStatistic(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
