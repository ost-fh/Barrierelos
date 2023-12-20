package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageReportEntity
import ch.barrierelos.backend.model.WebpageReport

public fun WebpageReport.toEntity(): WebpageReportEntity
{
  return WebpageReportEntity(
    webpageReportId = this.id,
    webpageFk = this.webpageId,
    report = this.report.toEntity(),
  )
}

public fun WebpageReportEntity.toModel(): WebpageReport
{
  return WebpageReport(
    id = this.webpageReportId,
    webpageId = this.webpageFk,
    report = this.report.toModel(),
  )
}

public fun WebpageReportEntity.toModel(webpageReport: WebpageReport): WebpageReport
{
  return webpageReport.apply {
    id = this@toModel.webpageReportId
    webpageId = this@toModel.webpageFk
    report = this@toModel.report.toModel(report)
  }
}

public fun Collection<WebpageReport>.toEntities(): MutableSet<WebpageReportEntity>
{
  return this.map { tag -> tag.toEntity() }.toMutableSet()
}

public fun Collection<WebpageReportEntity>.toModels(): MutableSet<WebpageReport>
{
  return this.map { tag -> tag.toModel() }.toMutableSet()
}
