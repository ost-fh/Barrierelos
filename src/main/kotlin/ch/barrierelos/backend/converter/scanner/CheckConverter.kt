package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.CheckEntity
import ch.barrierelos.backend.entity.scanner.RuleEntity
import ch.barrierelos.backend.message.scanner.CheckMessage
import ch.barrierelos.backend.model.scanner.Check

public fun CheckMessage.toEntity(rule: RuleEntity): CheckEntity
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
  )

  check.violatingElements = this.violatingElements.map { it.toEntity() }.toMutableSet()
  check.incompleteElements = this.incompleteElements.map { it.toEntity() }.toMutableSet()

  return check
}

public fun Check.toEntity(rule: RuleEntity): CheckEntity
{
  return CheckEntity(
    rule = rule,
    code = this.code,
    type = this.type,
    impact = this.impact,
    testedCount = this.testedCount,
    passedCount = this.passedCount,
    violatedCount = this.violatedCount,
    incompleteCount = this.incompleteCount,
    violatingElements = this.violatingElements.map { it.toEntity() }.toMutableSet(),
    incompleteElements = this.incompleteElements.map { it.toEntity() }.toMutableSet(),
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