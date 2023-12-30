package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.ScanJobEntity
import ch.barrierelos.backend.message.scanner.ScanJobMessage
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.scanner.ScanJob
import ch.barrierelos.backend.security.Security
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.sql.Timestamp


public fun Website.toScanJob(webpages: MutableSet<Webpage>): ScanJob
{
  return ScanJob(
    websiteId = this.id,
    userId = Security.getUserId(),
    jobTimestamp = Clock.System.now(),
    domain = this.domain,
    webpages = webpages.map { it.url }.toMutableSet(),
  )
}

public fun ScanJob.toMessage(): ScanJobMessage
{
  return ScanJobMessage(
    jobId = this.id,
    jobTimestamp = this.jobTimestamp,
    modelVersion = this.modelVersion,
    domain = this.domain,
    webpages = this.webpages,
  )
}

public fun ScanJob.toEntity(): ScanJobEntity
{
  return ScanJobEntity(
    scanJobId = this.id,
    websiteFk = this.websiteId,
    userFk = this.userId,
    modelVersion = this.modelVersion,
    jobTimestamp = Timestamp(this.jobTimestamp.toEpochMilliseconds()),
    domain = this.domain,
    webpages = this.webpages,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun ScanJobEntity.toModel(): ScanJob
{
  return ScanJob(
    id = this.scanJobId,
    websiteId = this.websiteFk,
    userId = this.userFk,
    modelVersion = this.modelVersion,
    jobTimestamp = Instant.fromEpochMilliseconds(this.jobTimestamp.time),
    domain = this.domain,
    webpages = this.webpages,
    modified = this.modified.time,
    created = this.created.time,
  )
}
