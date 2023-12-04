package ch.barrierelos.backend.converter.scanner

import ch.barrierelos.backend.entity.scanner.*
import ch.barrierelos.backend.message.scanner.*
import ch.barrierelos.backend.model.scanner.*
import java.sql.Timestamp


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
