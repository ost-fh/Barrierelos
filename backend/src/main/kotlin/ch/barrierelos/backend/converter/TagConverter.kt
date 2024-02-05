package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.TagEntity
import ch.barrierelos.backend.model.Tag

public fun Tag.toEntity(): TagEntity
{
  return TagEntity(
    tagId = this.id,
    name = this.name,
  )
}

public fun TagEntity.toModel(): Tag
{
  return Tag(
    id = this.tagId,
    name = this.name,
  )
}

public fun TagEntity.toModel(tag: Tag): Tag
{
  return tag.apply {
    id = this@toModel.tagId
    name = this@toModel.name
  }
}

public fun Collection<Tag>.toEntities(): MutableSet<TagEntity>
{
  return this.map { tag -> tag.toEntity() }.toMutableSet()
}

public fun Collection<TagEntity>.toModels(): MutableSet<Tag>
{
  return this.map { tag -> tag.toModel() }.toMutableSet()
}
