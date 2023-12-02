package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.model.WebsiteScan
import java.sql.Timestamp

public fun WebsiteScan.toEntity(): WebsiteScanEntity
{
  return WebsiteScanEntity(
    websiteScanId = this.id,
    websiteFk = this.websiteId,
    userFk = this.userId,
    scanResultFk = this.scanResultId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteScanEntity.toModel(): WebsiteScan
{
  return WebsiteScan(
    id = this.websiteScanId,
    websiteId = this.websiteFk,
    userId = this.userFk,
    scanResultId = this.scanResultFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteScanEntity.toModel(websiteScan: WebsiteScan): WebsiteScan
{
  return websiteScan.apply {
    id = this@toModel.websiteScanId
    websiteId = this@toModel.websiteFk
    userId = this@toModel.userFk
    scanResultId = this@toModel.scanResultFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
