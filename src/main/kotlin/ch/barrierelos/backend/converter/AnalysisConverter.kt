package ch.barrierelos.backend.converter

import ch.barrierelos.backend.constants.Scanner.MODEL_VERSION
import ch.barrierelos.backend.entity.scanner.*
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.*
import ch.barrierelos.backend.model.scanner.*
import kotlinx.datetime.Instant
import java.sql.Timestamp

public fun WebsiteMessage.toAnalysisJobEntity(): AnalysisJobEntity
{
  return AnalysisJobEntity(
    modelVersion = MODEL_VERSION,
    websiteBaseUrl = this.website,
    webpagePaths = this.webpages,
    modified = Timestamp(System.currentTimeMillis())
  )
}

public fun AnalysisJobEntity.toAnalysisJobMessage(): AnalysisJobMessage
{
  return AnalysisJobMessage(
    modelVersion = this.modelVersion,
    jobId = this.analysisJobId,
    jobTimestamp = this.modified.toInstant().toString(),
    websiteBaseUrl = this.websiteBaseUrl,
    webpagePaths = this.webpagePaths,
  )
}

public fun AnalysisJobEntity.toModel(): AnalysisJob
{
  return AnalysisJob(
    id = this.analysisJobId,
    modelVersion = this.modelVersion,
    websiteBaseUrl = this.websiteBaseUrl,
    webpagePaths = this.webpagePaths,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun AnalysisResultMessage.toEntity(analysisJobEntity: AnalysisJobEntity?): AnalysisResultEntity
{
  val analysisResult = AnalysisResultEntity(
    modelVersion = this.modelVersion,
    analysisJob = analysisJobEntity,
    website = this.website,
    scanTimestamp = Timestamp(Instant.parse(this.scanTimestamp).toEpochMilliseconds()),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = Timestamp(System.currentTimeMillis()),
    created = Timestamp(System.currentTimeMillis()),
  )

  analysisResult.webpages = this.webpages.map { it.toEntity(analysisResult, analysisResult.modified) }.toMutableSet()

  return analysisResult
}

public fun WebpageResultMessage.toEntity(analysisResult: AnalysisResultEntity, timestamp: Timestamp): WebpageResultEntity
{
  val webpageResult = WebpageResultEntity(
    analysisResult = analysisResult,
    path = this.path,
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = timestamp,
    created = timestamp,
  )

  webpageResult.rules = this.rules.map { it.toEntity(webpageResult, timestamp) }.toMutableSet()

  return webpageResult
}

public fun RuleMessage.toEntity(webpageResult: WebpageResultEntity, timestamp: Timestamp): RuleEntity
{
  val rule = RuleEntity(
    webpageResult = webpageResult,
    code = this.id,
    description = this.description,
    modified = timestamp,
    created = timestamp,
  )

  rule.checks = this.checks.map { it.toEntity(rule, timestamp) }.toMutableSet()

  return rule
}

public fun CheckMessage.toEntity(rule: RuleEntity, timestamp: Timestamp): CheckEntity
{
  val check = CheckEntity(
    rule = rule,
    code = this.id,
    type = this.type,
    impact = this.impact,
    testedCount = this.testedCount,
    passedCount = this.passedCount,
    violatedCount = this.violatedCount,
    incompleteCount = this.incompleteCount,
    modified = timestamp,
    created = timestamp,
  )

  check.violatingElements = this.violatingElements.map { it.toEntity(timestamp) }.toMutableSet()
  check.incompleteElements = this.incompleteElements.map { it.toEntity(timestamp) }.toMutableSet()

  return check
}

public fun CheckElementMessage.toEntity(timestamp: Timestamp): CheckElementEntity
{
  val checkElement = CheckElementEntity(
    target = this.target,
    html = this.html,
    issueDescription = this.issueDescription,
    data = this.data,
    modified = timestamp,
    created = timestamp,
  )

  checkElement.relatedElements = this.relatedElements.map { it.toEntity(checkElement, timestamp) }.toMutableSet()

  return checkElement
}

public fun ElementMessage.toEntity(checkElement: CheckElementEntity, timestamp: Timestamp): ElementEntity
{
  return ElementEntity(
    checkElement = checkElement,
    target = this.target,
    html = this.html,
    modified = timestamp,
    created = timestamp,
  )
}

public fun AnalysisResultEntity.toModel(): AnalysisResult
{
  return AnalysisResult(
    id = this.analysisResultId,
    modelVersion = this.modelVersion,
    analysisJob = this.analysisJob?.toModel(),
    website = this.website,
    scanTimestamp = Instant.fromEpochMilliseconds(this.scanTimestamp.time),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    webpages = this.webpages.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
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

public fun RuleEntity.toModel(): Rule
{
  return Rule(
    id = this.ruleId,
    code = this.code,
    description = this.description,
    checks = this.checks.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun CheckEntity.toModel(): Check
{
  return Check(
    id = this.checkId,
    code = this.code,
    type = this.type,
    impact = this.impact,
    testedCount = this.testedCount,
    passedCount = this.passedCount,
    violatedCount = this.violatedCount,
    incompleteCount = this.incompleteCount,
    violatingElements = this.violatingElements.map { it.toModel() }.toMutableSet(),
    incompleteElements = this.incompleteElements.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun CheckElementEntity.toModel(): CheckElement
{
  return CheckElement(
    id = this.checkElementId,
    target = this.target,
    html = this.html,
    issueDescription = this.issueDescription,
    data = this.data,
    relatedElements = this.relatedElements.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun ElementEntity.toModel(): Element
{
  return Element(
    id = this.elementId,
    target = this.target,
    html = this.html,
    modified = this.modified.time,
    created = this.created.time,
  )
}
