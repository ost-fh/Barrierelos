package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.entity.WebpageStatisticEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebpageStatistic
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.WebpageStatisticRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageStatisticService
{
  @Autowired
  private lateinit var webpageStatisticRepository: WebpageStatisticRepository

  public fun addWebpageStatistic(webpageStatistic: WebpageStatistic): WebpageStatistic
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val timestamp = System.currentTimeMillis()
    webpageStatistic.created = timestamp
    webpageStatistic.modified = timestamp

    return this.webpageStatisticRepository.save(webpageStatistic.toEntity()).toModel(webpageStatistic)
  }

  public fun updateWebpageStatistic(webpageStatistic: WebpageStatistic): WebpageStatistic
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val existingWebpage = this.webpageStatisticRepository.findById(webpageStatistic.id).orElseThrow().toModel()

    webpageStatistic.modified = System.currentTimeMillis()
    webpageStatistic.created = existingWebpage.created

    return this.webpageStatisticRepository.save(webpageStatistic.toEntity()).toModel()
  }

  public fun getWebpageStatistics(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageStatistic>
  {
    return this.webpageStatisticRepository.findAll(defaultParameters, WebpageStatisticEntity::class.java, WebpageStatisticEntity::toModel)
  }

  public fun getWebpageStatistic(webpageStatisticId: Long): WebpageStatistic
  {
    return this.webpageStatisticRepository.findById(webpageStatisticId).orElseThrow().toModel()
  }

  public fun deleteWebpageStatistic(webpageStatisticId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.webpageStatisticRepository.deleteById(webpageStatisticId)
  }
}
