package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.message.scanner.WebpageResultMessage
import ch.barrierelos.backend.model.scanner.WebpageResult
import java.sql.Timestamp

public fun WebpageResultMessage.toEntity(websiteResult: WebsiteResultEntity, timestamp: Timestamp): WebpageResultEntity
{
  val webpageResult = WebpageResultEntity(
    websiteResult = websiteResult,
    path = this.path,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = timestamp,
    created = timestamp,
  )

  webpageResult.rules = this.rules.map { it.toEntity(webpageResult, timestamp) }.toMutableSet()

  return webpageResult
}

public fun WebpageResultEntity.toModel(): WebpageResult
{
  return WebpageResult(
    id = this.webpageResultId,
    path = this.path,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    rules = this.rules.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}