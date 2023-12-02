package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteReportEntity
import ch.barrierelos.backend.model.WebsiteReport
import java.sql.Timestamp

public fun WebsiteReport.toEntity(): WebsiteReportEntity
{
  return WebsiteReportEntity(
    websiteReportId = this.id,
    websiteFk = this.websiteId,
    reportFk = this.reportId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteReportEntity.toModel(): WebsiteReport
{
  return WebsiteReport(
    id = this.websiteReportId,
    websiteId = this.websiteFk,
    reportId = this.reportFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteReportEntity.toModel(websiteReport: WebsiteReport): WebsiteReport
{
  return websiteReport.apply {
    id = this@toModel.websiteReportId
    websiteId = this@toModel.websiteFk
    reportId = this@toModel.reportFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
