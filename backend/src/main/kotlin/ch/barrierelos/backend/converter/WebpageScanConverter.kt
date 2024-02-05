package ch.barrierelos.backend.converter

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.entity.WebpageScanEntity
import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.message.WebpageScanMessage
import ch.barrierelos.backend.model.WebpageScan
import ch.barrierelos.backend.model.scanner.WebsiteResult
import java.sql.Timestamp

public fun WebpageScan.toMessage(): WebpageScanMessage
{
  return WebpageScanMessage(
    id = this.id,
    webpage = this.webpage,
    webpageStatistic = this.webpageStatistic,
    webpageResult = this.webpageResult,
    modified = this.modified,
    created = this.created,
  )
}

public fun WebpageScan.toEntity(websiteScan: WebsiteScanEntity, websiteResult: WebsiteResult? = null): WebpageScanEntity
{
  if(this.webpageResult != null && websiteResult == null)
    throw IllegalArgumentException("websiteResult must not be null if webpageResult is not null")

  return WebpageScanEntity(
    webpageScanId = this.id,
    websiteScan = websiteScan,
    webpage = this.webpage.toEntity(),
    webpageStatistic = this.webpageStatistic?.toEntity(),
    webpageResult = this.webpageResult?.toEntity(websiteResult!!),
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageScanEntity.toModel(): WebpageScan
{
  return WebpageScan(
    id = this.webpageScanId,
    webpage = this.webpage.toModel(),
    webpageStatistic = this.webpageStatistic?.toModel(),
    webpageResult = this.webpageResult?.toModel(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun Collection<WebpageScan>.toEntities(websiteScan: WebsiteScanEntity, websiteResult: WebsiteResult? = null): MutableSet<WebpageScanEntity>
{
  return this.map { webpageScan -> webpageScan.toEntity(websiteScan, websiteResult) }.toMutableSet()
}

public fun Collection<WebpageScanEntity>.toModels(): MutableSet<WebpageScan>
{
  return this.map { webpageScan -> webpageScan.toModel() }.toMutableSet()
}
