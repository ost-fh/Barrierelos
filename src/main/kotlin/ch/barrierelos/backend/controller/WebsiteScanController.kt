package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.WEBSITE_SCAN
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.model.WebsiteScan
import ch.barrierelos.backend.service.StatisticService
import ch.barrierelos.backend.service.WebsiteScanService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebsiteScanController
{
  @Autowired
  private lateinit var websiteScanService: WebsiteScanService

  @Autowired
  private lateinit var statisticService: StatisticService

  @PostMapping(value = [WEBSITE_SCAN], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsiteScan(@RequestBody websiteScan: WebsiteScan): ResponseEntity<WebsiteScan>
  {
    websiteScan.id = 0

    this.websiteScanService.addWebsiteScan(websiteScan)

    return ResponseEntity.status(HttpStatus.CREATED).body(websiteScan)
  }

  @PutMapping(value = ["$WEBSITE_SCAN/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebsiteScan(@PathVariable id: Long, @RequestBody websiteScan: WebsiteScan): ResponseEntity<WebsiteScan>
  {
    websiteScan.id = id

    this.websiteScanService.updateWebsiteScan(websiteScan)

    return ResponseEntity.status(HttpStatus.OK).body(websiteScan)
  }

  @GetMapping(value = [WEBSITE_SCAN], produces = [MediaType.JSON])
  public fun getWebsiteScans(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<WebsiteScan>>
  {
    val websiteScansPage = this.websiteScanService.getWebsiteScans(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websiteScansPage.toHeaders()).body(websiteScansPage.content)
  }

  @GetMapping(value = ["$WEBSITE_SCAN/{websiteId}"], produces = [MediaType.JSON])
  public fun getWebsiteScanByWebsiteId(@PathVariable websiteId: Long): ResponseEntity<WebsiteScanMessage>
  {
    val websiteScan = this.statisticService.getWebsiteScan(websiteId)

    return ResponseEntity.status(HttpStatus.OK).body(websiteScan)
  }

  @DeleteMapping(value = ["$WEBSITE_SCAN/{id}"], produces = [MediaType.JSON])
  public fun deleteWebsiteScan(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.websiteScanService.deleteWebsiteScan(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
