package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.ScanJobEntity
import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.message.scanner.WebsiteResultMessage
import ch.barrierelos.backend.model.scanner.WebsiteResult
import kotlinx.datetime.Instant
import java.sql.Timestamp

public fun WebsiteResultMessage.toEntity(scanJobEntity: ScanJobEntity): WebsiteResultEntity
{
  return WebsiteResultEntity(
    modelVersion = this.modelVersion,
    scanJob = scanJobEntity,
    domain = this.domain,
    scanTimestamp = Timestamp(this.scanTimestamp.toEpochMilliseconds()),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = Timestamp(System.currentTimeMillis()),
    created = Timestamp(System.currentTimeMillis()),
  )
}

public fun WebsiteResult.toEntity(): WebsiteResultEntity
{
  return WebsiteResultEntity(
    websiteResultId = this.id,
    modelVersion = this.modelVersion,
    scanJob = this.scanJob.toEntity(),
    domain = this.domain,
    scanTimestamp = Timestamp(this.scanTimestamp.toEpochMilliseconds()),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteResultEntity.toModel(): WebsiteResult
{
  return WebsiteResult(
    id = this.websiteResultId,
    modelVersion = this.modelVersion,
    scanJob = this.scanJob.toModel(),
    domain = this.domain,
    scanTimestamp = Instant.fromEpochMilliseconds(this.scanTimestamp.time),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = this.modified.time,
    created = this.created.time,
  )
}
