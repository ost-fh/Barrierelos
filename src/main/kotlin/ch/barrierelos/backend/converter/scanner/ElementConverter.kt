package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.CheckElementEntity
import ch.barrierelos.backend.entity.scanner.ElementEntity
import ch.barrierelos.backend.message.scanner.ElementMessage
import ch.barrierelos.backend.model.scanner.Element


public fun ElementMessage.toEntity(checkElement: CheckElementEntity): ElementEntity
{
  return ElementEntity(
    checkElement = checkElement,
    target = this.target,
    html = this.html,
  )
}

public fun Element.toEntity(checkElement: CheckElementEntity): ElementEntity
{
  return ElementEntity(
    checkElement = checkElement,
    target = this.target,
    html = this.html,
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
