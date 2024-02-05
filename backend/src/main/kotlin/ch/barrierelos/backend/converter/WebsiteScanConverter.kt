package ch.barrierelos.backend.converter

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.entity.WebsiteScanEntity
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.model.WebsiteScan
import java.sql.Timestamp

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

public fun WebsiteScanEntity.toModel(websiteScan: WebsiteScan): WebsiteScan
{
  return websiteScan.apply {
    id = this@toModel.websiteScanId
    website = this@toModel.website.toModel()
    websiteStatistic = this@toModel.websiteStatistic?.toModel()
    websiteResult = this@toModel.websiteResult?.toModel()
    webpageScans = this@toModel.webpageScans.map { it.toModel() }.toMutableSet()
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
