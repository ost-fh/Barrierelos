package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBPAGE
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageService
import ch.barrierelos.backend.util.toHeaders
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebpageController
{
  @Autowired
  private lateinit var webpageService: WebpageService

  @PostMapping(value = [WEBPAGE], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addWebpage(@RequestBody webpage: Webpage): ResponseEntity<Webpage>
  {
    webpage.id = 0

    this.webpageService.addWebpage(webpage)

    return ResponseEntity.status(HttpStatus.CREATED).body(webpage)
  }

  @PutMapping(value = ["${WEBPAGE}/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateWebpage(@PathVariable id: Long, @RequestBody webpage: Webpage): ResponseEntity<Webpage>
  {
    webpage.id = id

    this.webpageService.updateWebpage(webpage)

    return ResponseEntity.status(HttpStatus.OK).body(webpage)
  }

  @GetMapping(value = [WEBPAGE], produces = [MediaType.JSON])
  public fun getWebpages(@ParameterObject defaultParameters: DefaultParameters): ResponseEntity<List<Webpage>>
  {
    val webpagePage = this.webpageService.getWebpages(defaultParameters)

    return ResponseEntity.status(HttpStatus.OK).headers(webpagePage.toHeaders()).body(webpagePage.content)
  }

  @GetMapping(value = ["${WEBPAGE}/{id}"], produces = [MediaType.JSON])
  public fun getWebpage(@PathVariable id: Long): ResponseEntity<Webpage>
  {
    val webpage: Webpage = this.webpageService.getWebpage(id)

    return ResponseEntity.status(HttpStatus.OK).body(webpage)
  }

  @DeleteMapping(value = ["${WEBPAGE}/{id}"], produces = [MediaType.JSON])
  public fun deleteWebpage(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.webpageService.deleteWebpage(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
