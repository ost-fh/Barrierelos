package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint
import ch.barrierelos.backend.constants.Endpoint.WEBSITE
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteService
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
  public fun addWebsite(@RequestBody website: Website): ResponseEntity<Website>
  {
    website.id = 0

    this.websiteService.addWebsite(website)

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
  public fun getWebsites(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<Website>>
  {
    val websitePage = this.websiteService.getWebsites(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(websitePage.toHeaders()).body(websitePage.content)
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

  @PostMapping(value = [Endpoint.WEBSITES], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebsite(@RequestBody website: WebsiteMessage): ResponseEntity<Any>
  {
    this.websiteService.scanWebsite(website)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
