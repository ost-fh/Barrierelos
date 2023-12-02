package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.UserReportEntity
import ch.barrierelos.backend.model.UserReport
import java.sql.Timestamp

public fun UserReport.toEntity(): UserReportEntity
{
  return UserReportEntity(
    userReportId = this.id,
    userFk = this.userId,
    reportFk = this.reportId,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun UserReportEntity.toModel(): UserReport
{
  return UserReport(
    id = this.userReportId,
    userId = this.userFk,
    reportId = this.reportFk,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun UserReportEntity.toModel(userReport: UserReport): UserReport
{
  return userReport.apply {
    id = this@toModel.userReportId
    userId = this@toModel.userFk
    reportId = this@toModel.reportFk
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
