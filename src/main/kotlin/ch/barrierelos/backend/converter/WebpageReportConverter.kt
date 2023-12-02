package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageReportEntity
import ch.barrierelos.backend.model.WebpageReport
import java.sql.Timestamp

public fun WebpageReport.toEntity(): WebpageReportEntity
{
  return WebpageReportEntity(
    webpageReportId = this.id,
    webpageFk = this.webpageId,
    reportFk = this.reportId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageReportEntity.toModel(): WebpageReport
{
  return WebpageReport(
    id = this.webpageReportId,
    webpageId = this.webpageFk,
    reportId = this.reportFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageReportEntity.toModel(webpageReport: WebpageReport): WebpageReport
{
  return webpageReport.apply {
    id = this@toModel.webpageReportId
    webpageId = this@toModel.webpageFk
    reportId = this@toModel.reportFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
