package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.RuleEntity
import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.message.scanner.RuleMessage
import ch.barrierelos.backend.model.scanner.Rule
import java.sql.Timestamp

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