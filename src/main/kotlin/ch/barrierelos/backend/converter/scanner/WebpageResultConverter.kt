package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.message.scanner.WebpageResultMessage
import ch.barrierelos.backend.model.scanner.WebpageResult

public fun WebpageResultMessage.toEntity(websiteResult: WebsiteResultEntity): WebpageResultEntity
{
  val webpageResult = WebpageResultEntity(
    websiteResult = websiteResult,
    path = this.path,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
  )

  webpageResult.rules = this.rules.map { it.toEntity(webpageResult) }.toMutableSet()

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