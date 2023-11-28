package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constants.Endpoint.WEBSITE
import ch.barrierelos.backend.constants.MediaType
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.service.DetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class WebsiteController
{
  @Autowired
  private lateinit var detailsService: DetailsService

  @GetMapping(value = ["${WEBSITE}/{id}"], produces = [MediaType.JSON])
  public fun getWebsite(@PathVariable id: Long): ResponseEntity<WebsiteDetails>
  {
    val websiteDetails = detailsService.getWebsiteDetails(id)

    return ResponseEntity.status(HttpStatus.OK).body(websiteDetails)
  }
}
