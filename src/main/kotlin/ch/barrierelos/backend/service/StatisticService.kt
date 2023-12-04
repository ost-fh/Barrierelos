package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.repository.WebsiteDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class StatisticService
{
  @Autowired
  private lateinit var websiteDetailsRepository: WebsiteDetailsRepository

  public fun getWebsiteDetails(id: Long): WebsiteDetails = websiteDetailsRepository.findById(id).orElseThrow().toModel()
}
