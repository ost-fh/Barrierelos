package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.model.WebsiteScan
import java.sql.Timestamp

public fun WebsiteScan.toEntity(): WebsiteScanEntity
{
  return WebsiteScanEntity(
    websiteScanId = this.id,
    websiteFk = this.websiteId,
    websiteStatisticFk = this.websiteStatisticId,
    websiteResultFk = this.websiteResultId,
    userFk = this.userId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteScanEntity.toModel(): WebsiteScan
{
  return WebsiteScan(
    id = this.websiteScanId,
    websiteId = this.websiteFk,
    websiteStatisticId = this.websiteStatisticFk,
    websiteResultId = this.websiteResultFk,
    userId = this.userFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteScanEntity.toModel(websiteScan: WebsiteScan): WebsiteScan
{
  return websiteScan.apply {
    id = this@toModel.websiteScanId
    websiteId = this@toModel.websiteFk
    websiteStatisticId = this@toModel.websiteStatisticFk
    websiteResultId = this@toModel.websiteResultFk
    userId = this@toModel.userFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<WebsiteScan>.toEntities(): MutableSet<WebsiteScanEntity>
{
  return this.map { websiteScan -> websiteScan.toEntity() }.toMutableSet()
}

public fun Collection<WebsiteScanEntity>.toModels(): MutableSet<WebsiteScan>
{
  return this.map { websiteScan -> websiteScan.toModel() }.toMutableSet()
}
