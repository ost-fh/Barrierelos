package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.ReportEntity
import ch.barrierelos.backend.model.Report
import java.sql.Timestamp

public fun Report.toEntity(): ReportEntity
{
  return ReportEntity(
    reportId = this.id,
    userFk = this.userId,
    reason = this.reason,
    status = this.status,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun ReportEntity.toModel(): Report
{
  return Report(
    id = this.reportId,
    userId = this.userFk,
    reason = this.reason,
    status = this.status,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun ReportEntity.toModel(report: Report): Report
{
  return report.apply {
    id = this@toModel.reportId
    userId = this@toModel.userFk
    reason = this@toModel.reason
    status = this@toModel.status
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
