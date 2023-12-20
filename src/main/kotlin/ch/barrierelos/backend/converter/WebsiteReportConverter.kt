package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteReportEntity
import ch.barrierelos.backend.model.WebsiteReport

public fun WebsiteReport.toEntity(): WebsiteReportEntity
{
  return WebsiteReportEntity(
    websiteReportId = this.id,
    websiteFk = this.websiteId,
    report = this.report.toEntity(),
  )
}

public fun WebsiteReportEntity.toModel(): WebsiteReport
{
  return WebsiteReport(
    id = this.websiteReportId,
    websiteId = this.websiteFk,
    report = this.report.toModel(),
  )
}

public fun WebsiteReportEntity.toModel(websiteReport: WebsiteReport): WebsiteReport
{
  return websiteReport.apply {
    id = this@toModel.websiteReportId
    websiteId = this@toModel.websiteFk
    report = this@toModel.report.toModel(report)
  }
}

public fun Collection<WebsiteReport>.toEntities(): MutableSet<WebsiteReportEntity>
{
  return this.map { tag -> tag.toEntity() }.toMutableSet()
}

public fun Collection<WebsiteReportEntity>.toModels(): MutableSet<WebsiteReport>
{
  return this.map { tag -> tag.toModel() }.toMutableSet()
}
