package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.entity.WebpageScanEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebpageScan
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.WebpageScanRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageScanService
{
  @Autowired
  private lateinit var webpageScanRepository: WebpageScanRepository

  public fun addWebpageScan(webpageScan: WebpageScan): WebpageScan
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val timestamp = System.currentTimeMillis()
    webpageScan.created = timestamp
    webpageScan.modified = timestamp

    return this.webpageScanRepository.save(webpageScan.toEntity()).toModel()
  }

  public fun updateWebpageScan(webpageScan: WebpageScan): WebpageScan
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val existingWebpage = this.webpageScanRepository.findById(webpageScan.id).orElseThrow().toModel()

    webpageScan.modified = System.currentTimeMillis()
    webpageScan.created = existingWebpage.created

    return this.webpageScanRepository.save(webpageScan.toEntity()).toModel()
  }

  public fun getWebpageScans(webpageId: Long): Set<WebpageScan>
  {
    return this.webpageScanRepository.findAllByWebpageFk(webpageId).toModels()
  }

  public fun getWebpageScans(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageScan>
  {
    return this.webpageScanRepository.findAll(defaultParameters, WebpageScanEntity::class.java, WebpageScanEntity::toModel)
  }

  public fun getWebpageScan(webpageScanId: Long): WebpageScan
  {
    return this.webpageScanRepository.findById(webpageScanId).orElseThrow().toModel()
  }

  public fun deleteWebpageScan(webpageScanId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.webpageScanRepository.deleteById(webpageScanId)
  }
}
