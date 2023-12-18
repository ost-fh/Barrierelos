package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageScanEntity
import ch.barrierelos.backend.model.WebpageScan
import java.sql.Timestamp

public fun WebpageScan.toEntity(): WebpageScanEntity
{
  return WebpageScanEntity(
    webpageScanId = this.id,
    webpageFk = this.webpageId,
    webpageStatisticFk = this.webpageStatisticId,
    webpageResultFk = this.webpageResultId,
    userFk = this.userId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageScanEntity.toModel(): WebpageScan
{
  return WebpageScan(
    id = this.webpageScanId,
    webpageId = this.webpageFk,
    webpageStatisticId = this.webpageStatisticFk,
    webpageResultId = this.webpageResultFk,
    userId = this.userFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageScanEntity.toModel(webpageScan: WebpageScan): WebpageScan
{
  return webpageScan.apply {
    id = this@toModel.webpageScanId
    webpageId = this@toModel.webpageFk
    webpageStatisticId = this@toModel.webpageStatisticFk
    webpageResultId = this@toModel.webpageResultFk
    userId = this@toModel.userFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<WebpageScan>.toEntities(): MutableSet<WebpageScanEntity>
{
  return this.map { webpageScan -> webpageScan.toEntity() }.toMutableSet()
}

public fun Collection<WebpageScanEntity>.toModels(): MutableSet<WebpageScan>
{
  return this.map { webpageScan -> webpageScan.toModel() }.toMutableSet()
}
