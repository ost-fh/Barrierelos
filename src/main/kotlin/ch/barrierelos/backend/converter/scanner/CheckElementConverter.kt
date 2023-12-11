package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.CheckElementEntity
import ch.barrierelos.backend.message.scanner.CheckElementMessage
import ch.barrierelos.backend.model.scanner.CheckElement

public fun CheckElementMessage.toEntity(): CheckElementEntity
{
  val checkElement = CheckElementEntity(
    target = this.target,
    html = this.html,
    issueDescription = this.issueDescription,
    data = this.data,
  )

  checkElement.relatedElements = this.relatedElements.map { it.toEntity(checkElement) }.toMutableSet()

  return checkElement
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