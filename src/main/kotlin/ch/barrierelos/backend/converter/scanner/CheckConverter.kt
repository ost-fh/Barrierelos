package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.CheckEntity
import ch.barrierelos.backend.entity.scanner.RuleEntity
import ch.barrierelos.backend.message.scanner.CheckMessage
import ch.barrierelos.backend.model.scanner.Check
import java.sql.Timestamp

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