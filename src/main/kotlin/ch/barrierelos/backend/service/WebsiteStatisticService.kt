package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebsiteStatisticEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebsiteStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.WebsiteStatisticRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebsiteStatisticService
{
  @Autowired
  private lateinit var websiteStatisticRepository: WebsiteStatisticRepository

  public fun addWebsiteStatistic(websiteStatistic: WebsiteStatistic): WebsiteStatistic
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val timestamp = System.currentTimeMillis()
    websiteStatistic.created = timestamp
    websiteStatistic.modified = timestamp

    return this.websiteStatisticRepository.save(websiteStatistic.toEntity()).toModel()
  }

  public fun updateWebsiteStatistic(websiteStatistic: WebsiteStatistic): WebsiteStatistic
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val existingWebsite = this.websiteStatisticRepository.findById(websiteStatistic.id).orElseThrow().toModel()

    websiteStatistic.modified = System.currentTimeMillis()
    websiteStatistic.created = existingWebsite.created

    return this.websiteStatisticRepository.save(websiteStatistic.toEntity()).toModel()
  }

  public fun getWebsiteStatistics(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteStatistic>
  {
    return this.websiteStatisticRepository.findAll(defaultParameters, WebsiteStatisticEntity::class.java, WebsiteStatisticEntity::toModel)
  }

  public fun getWebsiteStatistic(websiteStatisticId: Long): WebsiteStatistic
  {
    return this.websiteStatisticRepository.findById(websiteStatisticId).orElseThrow().toModel()
  }

  public fun deleteWebsiteStatistic(websiteStatisticId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.websiteStatisticRepository.deleteById(websiteStatisticId)
  }
}
