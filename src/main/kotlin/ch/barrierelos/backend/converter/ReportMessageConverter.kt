package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.ReportMessageEntity
import ch.barrierelos.backend.model.ReportMessage
import java.sql.Timestamp

public fun ReportMessage.toEntity(): ReportMessageEntity
{
  return ReportMessageEntity(
    reportMessageId = this.id,
    reportFk = this.reportId,
    userFk = this.userId,
    message = this.message,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun ReportMessageEntity.toModel(): ReportMessage
{
  return ReportMessage(
    id = this.reportMessageId,
    reportId = this.reportFk,
    userId = this.userFk,
    message = this.message,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun ReportMessageEntity.toModel(reportMessage: ReportMessage): ReportMessage
{
  return reportMessage.apply {
    id = this@toModel.reportMessageId
    reportId = this@toModel.reportFk
    userId = this@toModel.userFk
    message = this@toModel.message
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<ReportMessage>.toEntities(): MutableSet<ReportMessageEntity>
{
  return this.map { reportMessage -> reportMessage.toEntity() }.toMutableSet()
}

public fun Collection<ReportMessageEntity>.toModels(): MutableSet<ReportMessage>
{
  return this.map { reportMessage -> reportMessage.toModel() }.toMutableSet()
}
