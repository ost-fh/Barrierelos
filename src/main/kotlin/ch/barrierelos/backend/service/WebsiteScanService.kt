package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.WebsiteScan
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.WebsiteScanRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebsiteScanService
{
  @Autowired
  private lateinit var websiteScanRepository: WebsiteScanRepository

  public fun addWebsiteScan(websiteScan: WebsiteScan): WebsiteScan
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val timestamp = System.currentTimeMillis()
    websiteScan.created = timestamp
    websiteScan.modified = timestamp

    return this.websiteScanRepository.save(websiteScan.toEntity()).toModel(websiteScan)
  }

  public fun updateWebsiteScan(websiteScan: WebsiteScan): WebsiteScan
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val existingWebsite = this.websiteScanRepository.findById(websiteScan.id).orElseThrow().toModel()

    websiteScan.modified = System.currentTimeMillis()
    websiteScan.created = existingWebsite.created

    return this.websiteScanRepository.save(websiteScan.toEntity()).toModel()
  }

  public fun getWebsiteScans(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteScan>
  {
    return this.websiteScanRepository.findAll(defaultParameters, WebsiteScanEntity::class.java, WebsiteScanEntity::toModel)
  }

  public fun getWebsiteScan(websiteScanId: Long): WebsiteScan
  {
    return this.websiteScanRepository.findById(websiteScanId).orElseThrow().toModel()
  }

  public fun deleteWebsiteScan(websiteScanId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.websiteScanRepository.deleteById(websiteScanId)
  }
}
