package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageScanEntity
import ch.barrierelos.backend.model.WebpageScan
import java.sql.Timestamp

public fun WebpageScan.toEntity(): WebpageScanEntity
{
  return WebpageScanEntity(
    webpageScanId = this.id,
    webpageFk = this.webpageId,
    userFk = this.userId,
    scanResultFk = this.scanResultId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageScanEntity.toModel(): WebpageScan
{
  return WebpageScan(
    id = this.webpageScanId,
    webpageId = this.webpageFk,
    userId = this.userFk,
    scanResultId = this.scanResultFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageScanEntity.toModel(webpageScan: WebpageScan): WebpageScan
{
  return webpageScan.apply {
    id = this@toModel.webpageScanId
    webpageId = this@toModel.webpageFk
    userId = this@toModel.userFk
    scanResultId = this@toModel.scanResultFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
