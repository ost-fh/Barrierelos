package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.constants.Scanner
import ch.barrierelos.backend.entity.scanner.ScanJobEntity
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.ScanJobMessage
import ch.barrierelos.backend.model.scanner.ScanJob
import java.sql.Timestamp


public fun WebsiteMessage.toScanJobEntity(): ScanJobEntity
{
  return ScanJobEntity(
    modelVersion = Scanner.MODEL_VERSION,
    locale = this.locale ?: Scanner.DEFAULT_LOCALE,
    websiteBaseUrl = this.website,
    webpagePaths = this.webpages,
    modified = Timestamp(System.currentTimeMillis()),
    created = Timestamp(System.currentTimeMillis()),
  )
}

public fun ScanJobEntity.toModel(): ScanJob
{
  return ScanJob(
    id = this.scanJobId,
    modelVersion = this.modelVersion,
    locale = this.locale,
    websiteBaseUrl = this.websiteBaseUrl,
    webpagePaths = this.webpagePaths,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun ScanJob.toMessage(): ScanJobMessage
{
  return ScanJobMessage(
    jobId = this.id,
    jobTimestamp = this.jobTimestamp,
    modelVersion = this.modelVersion,
    locale = this.locale,
    websiteBaseUrl = this.websiteBaseUrl,
    webpagePaths = this.webpagePaths,
  )
}
