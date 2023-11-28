package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.model.WebsiteDetails
import ch.barrierelos.backend.repository.WebsiteDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class DetailsService {
    @Autowired
    private lateinit var analysisJobRepository: WebsiteDetailsRepository

    public fun getWebsiteDetails(id: Long): WebsiteDetails = analysisJobRepository.findById(id).orElseThrow().toModel()
}
