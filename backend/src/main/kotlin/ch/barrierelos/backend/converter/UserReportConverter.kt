package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.UserReportEntity
import ch.barrierelos.backend.model.UserReport

public fun UserReport.toEntity(): UserReportEntity
{
  return UserReportEntity(
    userReportId = this.id,
    userFk = this.userId,
    report = this.report.toEntity(),
  )
}

public fun UserReportEntity.toModel(): UserReport
{
  return UserReport(
    id = this.userReportId,
    userId = this.userFk,
    report = this.report.toModel(),
  )
}

public fun UserReportEntity.toModel(userReport: UserReport): UserReport
{
  return userReport.apply {
    id = this@toModel.userReportId
    userId = this@toModel.userFk
    report = this@toModel.report.toModel(report)
  }
}

public fun Collection<UserReport>.toEntities(): MutableSet<UserReportEntity>
{
  return this.map { tag -> tag.toEntity() }.toMutableSet()
}

public fun Collection<UserReportEntity>.toModels(): MutableSet<UserReport>
{
  return this.map { tag -> tag.toModel() }.toMutableSet()
}
