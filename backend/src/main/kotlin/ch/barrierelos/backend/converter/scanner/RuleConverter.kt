package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.RuleEntity
import ch.barrierelos.backend.entity.scanner.WcagReferencesEntity
import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.message.scanner.RuleMessage
import ch.barrierelos.backend.message.scanner.WcagReferencesMessage
import ch.barrierelos.backend.model.scanner.Rule
import ch.barrierelos.backend.model.scanner.WcagReferences
import java.sql.Timestamp

public fun RuleMessage.toEntity(webpageResult: WebpageResultEntity): RuleEntity
{
  val rule = RuleEntity(
    webpageResult = webpageResult,
    code = this.id,
    description = this.description,
    axeUrl = this.axeUrl,
  )

  rule.wcagReferences = this.wcagReferences?.toEntity(rule)
  rule.checks = this.checks.map { it.toEntity(rule) }.toMutableSet()

  return rule
}

public fun Rule.toEntity(webpageResult: WebpageResultEntity): RuleEntity
{
  val rule = RuleEntity(
    webpageResult = webpageResult,
    code = this.code,
    axeUrl = this.axeUrl,
    description = this.description,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )

  rule.wcagReferences = this.wcagReferences?.toEntity(rule)
  rule.checks = this.checks.map { it.toEntity(rule) }.toMutableSet()

  return rule
}

public fun RuleEntity.toModel(): Rule
{
  return Rule(
    id = this.ruleId,
    code = this.code,
    description = this.description,
    axeUrl = this.axeUrl,
    wcagReferences = this.wcagReferences?.toModel(),
    checks = this.checks.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WcagReferencesMessage.toEntity(ruleEntity: RuleEntity): WcagReferencesEntity
{
  return WcagReferencesEntity(
    rule = ruleEntity,
    version = this.version,
    level = this.level,
    criteria = this.criteria.toMutableSet(),
  )
}

public fun WcagReferences.toEntity(rule: RuleEntity): WcagReferencesEntity
{
  return WcagReferencesEntity(
    rule = rule,
    version = this.version,
    level = this.level,
    criteria = this.criteria.toMutableSet(),
  )
}

public fun WcagReferencesEntity.toModel(): WcagReferences
{
  return WcagReferences(
    id = this.wcagReferencesId,
    version = this.version,
    level = this.level,
    criteria = this.criteria.toMutableSet(),
  )
}
