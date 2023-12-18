package ch.barrierelos.backend.converter

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.entity.WebpageScanEntity
import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.message.WebpageScanMessage
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.model.WebpageScan
import ch.barrierelos.backend.model.WebsiteScan
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

public fun WebsiteScan.toMessage(): WebsiteScanMessage
{
  return WebsiteScanMessage(
    id = this.id,
    website = this.website,
    websiteStatistic = this.websiteStatistic,
    websiteResult = this.websiteResult,
    webpageScans = this.webpageScans.map { it.toMessage() }.toMutableSet(),
  )
}

public fun WebsiteScan.toEntity(): WebsiteScanEntity
{
  val websiteScan = WebsiteScanEntity(
    websiteScanId = this.id,
    website = this.website.toEntity(),
    websiteStatistic = this.websiteStatistic?.toEntity(),
    websiteResult = this.websiteResult?.toEntity(),
    webpageScans = mutableSetOf(),
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
  websiteScan.webpageScans = this.webpageScans.map { it.toEntity(websiteScan, websiteResult) }.toMutableSet()
  return websiteScan
}

public fun WebsiteScanEntity.toModel(): WebsiteScan
{
  val websiteScan = WebsiteScan(
    id = this.websiteScanId,
    website = this.website.toModel(),
    websiteStatistic = this.websiteStatistic?.toModel(),
    websiteResult = this.websiteResult?.toModel(),
    webpageScans = mutableSetOf(),
    modified = this.modified.time,
    created = this.created.time,
  )
  websiteScan.webpageScans = this.webpageScans.map { it.toModel() }.toMutableSet()
  return websiteScan
}
