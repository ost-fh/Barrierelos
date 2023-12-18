package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBPAGE_SCAN
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebpageScan
import ch.barrierelos.backend.model.WebpageStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageScanService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebpageScanController
{
  @Autowired
  private lateinit var webpageScanService: WebpageScanService

  @Autowired
  private lateinit var webpageStatisticController: WebpageStatisticController

  @PostMapping(value = [WEBPAGE_SCAN], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebpageScan(@RequestBody webpageScan: WebpageScan): ResponseEntity<WebpageScan>
  {
    webpageScan.id = 0

    this.webpageScanService.addWebpageScan(webpageScan)

    return ResponseEntity.status(HttpStatus.CREATED).body(webpageScan)
  }

  @PutMapping(value = ["$WEBPAGE_SCAN/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebpageScan(@PathVariable id: Long, @RequestBody webpageScan: WebpageScan): ResponseEntity<WebpageScan>
  {
    webpageScan.id = id

    this.webpageScanService.updateWebpageScan(webpageScan)

    return ResponseEntity.status(HttpStatus.OK).body(webpageScan)
  }

  @GetMapping(value = [WEBPAGE_SCAN], produces = [MediaType.JSON])
  public fun getWebpageScans(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebpageScan>>
  {
    val webpageScansPage = this.webpageScanService.getWebpageScans(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(webpageScansPage.toHeaders()).body(webpageScansPage.content)
  }

  @GetMapping(value = ["$WEBPAGE_SCAN/{id}"], produces = [MediaType.JSON])
  public fun getWebpageScan(@PathVariable id: Long): ResponseEntity<WebpageScan>
  {
    val webpageScan: WebpageScan = this.webpageScanService.getWebpageScan(id)

    return ResponseEntity.status(HttpStatus.OK).body(webpageScan)
  }

  @GetMapping(value = ["$WEBPAGE_SCAN/{id}/statistic"], produces = [MediaType.JSON])
  public fun getWebpageScanStatistic(@PathVariable id: Long): ResponseEntity<WebpageStatistic>
  {
    val webpageScan: WebpageScan = this.webpageScanService.getWebpageScan(id)

    return this.webpageStatisticController.getWebpageStatistic(webpageScan.webpageStatisticId)
  }

  @DeleteMapping(value = ["$WEBPAGE_SCAN/{id}"], produces = [MediaType.JSON])
  public fun deleteWebpageScan(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.webpageScanService.deleteWebpageScan(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
