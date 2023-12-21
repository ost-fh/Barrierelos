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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
public class WebpageScanController
{
  @Autowired
  private lateinit var webpageScanService: WebpageScanService

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

    return ResponseEntity.status(HttpStatus.OK).body(webpageScan.webpageStatistic)
  }

  @DeleteMapping(value = ["$WEBPAGE_SCAN/{id}"], produces = [MediaType.JSON])
  public fun deleteWebpageScan(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.webpageScanService.deleteWebpageScan(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
