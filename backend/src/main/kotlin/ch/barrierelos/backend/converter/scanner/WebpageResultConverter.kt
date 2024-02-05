package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.message.scanner.WebpageResultMessage
import ch.barrierelos.backend.model.scanner.WebpageResult
import ch.barrierelos.backend.model.scanner.WebsiteResult
import java.sql.Timestamp

public fun WebpageResultMessage.toEntity(websiteResult: WebsiteResultEntity): WebpageResultEntity
{
  val webpageResult = WebpageResultEntity(
    websiteResult = websiteResult,
    url = this.url,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
  )

  webpageResult.rules = this.rules.map { it.toEntity(webpageResult) }.toMutableSet()

  return webpageResult
}

public fun WebpageResult.toEntity(websiteResult: WebsiteResult): WebpageResultEntity
{
  val webpageResult = WebpageResultEntity(
    webpageResultId = this.id,
    websiteResult = websiteResult.toEntity(),
    url = this.url,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )

  webpageResult.rules = this.rules.map { it.toEntity(webpageResult) }.toMutableSet()

  return webpageResult
}

public fun WebpageResultEntity.toModel(): WebpageResult
{
  return WebpageResult(
    id = this.webpageResultId,
    url = this.url,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    rules = this.rules.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}
