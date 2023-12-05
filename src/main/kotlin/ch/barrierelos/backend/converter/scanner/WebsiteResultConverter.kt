package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.ScanJobEntity
import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.message.scanner.WebsiteResultMessage
import ch.barrierelos.backend.model.scanner.WebsiteResult
import kotlinx.datetime.Instant
import java.sql.Timestamp

public fun WebsiteResultMessage.toEntity(scanJobEntity: ScanJobEntity?): WebsiteResultEntity
{
  return WebsiteResultEntity(
    modelVersion = modelVersion,
    scanJob = scanJobEntity,
    website = website,
    scanTimestamp = Timestamp(scanTimestamp.toEpochMilliseconds()),
    scanStatus = scanStatus,
    errorMessage = errorMessage,
    modified = Timestamp(System.currentTimeMillis()),
    created = Timestamp(System.currentTimeMillis()),
  )
}

public fun WebsiteResultEntity.toModel(): WebsiteResult
{
  return WebsiteResult(
    id = this.websiteResultId,
    modelVersion = this.modelVersion,
    scanJob = this.scanJob?.toModel(),
    website = this.website,
    scanTimestamp = Instant.fromEpochMilliseconds(this.scanTimestamp.time),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = this.modified.time,
    created = this.created.time,
  )
}
