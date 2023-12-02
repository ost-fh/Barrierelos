package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.ReportMessageEntity
import ch.barrierelos.backend.model.ReportMessage
import java.sql.Timestamp

public fun ReportMessage.toEntity(): ReportMessageEntity
{
  return ReportMessageEntity(
    reportMessageId = this.id,
    userFk = this.userId,
    reportFk = this.reportId,
    message = this.message,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun ReportMessageEntity.toModel(): ReportMessage
{
  return ReportMessage(
    id = this.reportMessageId,
    userId = this.userFk,
    reportId = this.reportFk,
    message = this.message,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun ReportMessageEntity.toModel(reportMessage: ReportMessage): ReportMessage
{
  return reportMessage.apply {
    id = this@toModel.reportMessageId
    userId = this@toModel.userFk
    reportId = this@toModel.reportFk
    message = this@toModel.message
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
