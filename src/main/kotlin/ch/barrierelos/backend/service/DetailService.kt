package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.repository.WebpageDetailsRepository
import ch.barrierelos.backend.repository.WebsiteDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
public class DetailService
{
  @Autowired
  private lateinit var websiteDetailsRepository: WebsiteDetailsRepository

  @Autowired
  private lateinit var webpageDetailsRepository: WebpageDetailsRepository

  public fun getWebsiteDetails(websiteId: Long): WebsiteDetails
  {
    val websiteDetailsEntity = websiteDetailsRepository.findMostRecentByWebsiteId(websiteId)
    val webpageDetailsEntities = webpageDetailsRepository.findMostRecentByWebsiteId(websiteId, Pageable.ofSize(websiteDetailsEntity.website.webpageCount))
    return websiteDetailsEntity.toModel(webpageDetailsEntities.toMutableSet())
  }
}
