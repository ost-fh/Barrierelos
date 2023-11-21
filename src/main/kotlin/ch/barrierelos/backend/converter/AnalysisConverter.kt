package ch.barrierelos.backend.converter

import ch.barrierelos.backend.constants.Scanner.MODEL_VERSION
import ch.barrierelos.backend.entity.scanner.*
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.message.scanner.*
import ch.barrierelos.backend.model.scanner.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

public fun WebsiteMessage.toAnalysisJobMessage(): AnalysisJobMessage
{
  return AnalysisJobMessage(
    modelVersion = MODEL_VERSION,
    jobTimestamp = DateTimeFormatter.ISO_INSTANT.format(Clock.System.now().toJavaInstant()),
    websiteBaseUrl = this.website,
    webpagePaths = this.webpages,
  )
}

public fun AnalysisJobMessage.toEntity(): AnalysisJobEntity
{
  return AnalysisJobEntity(
    modelVersion = this.modelVersion,
    websiteBaseUrl = this.websiteBaseUrl,
    webpagePaths = this.webpagePaths,
    modified = Timestamp.from(Instant.parse(this.jobTimestamp).toJavaInstant()),
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
  )
}

public fun AnalysisResultMessage.toEntity(): AnalysisResultEntity
{
  val analysisResult = AnalysisResultEntity(
    modelVersion = this.modelVersion,
    website = this.website,
    scanTimestamp = Timestamp(Instant.parse(this.scanTimestamp).toEpochMilliseconds()),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    modified = Timestamp(System.currentTimeMillis()),
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
  )
  webpageResult.rules = this.rules.map { it.toEntity(webpageResult, timestamp) }.toMutableSet()
  return webpageResult
}

public fun RuleMessage.toEntity(webpageResult: WebpageResultEntity, timestamp: Timestamp): RuleEntity
{
  val rule = RuleEntity(
    webpageResult = webpageResult,
    code = this.id,
    modified = timestamp,
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
  )
}

public fun AnalysisResultEntity.toModel(): AnalysisResult
{
  return AnalysisResult(
    id = this.analysisResultId,
    modelVersion = this.modelVersion,
    website = this.website,
    scanTimestamp = Instant.fromEpochMilliseconds(this.scanTimestamp.time),
    scanStatus = this.scanStatus,
    errorMessage = this.errorMessage,
    webpages = this.webpages.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
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
  )
}

public fun RuleEntity.toModel(): Rule
{
  return Rule(
    id = this.ruleId,
    code = this.code,
    checks = this.checks.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
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
  )
}

public fun ElementEntity.toModel(): Element
{
  return Element(
    id = this.elementId,
    target = this.target,
    html = this.html,
    modified = this.modified.time,
  )
}
