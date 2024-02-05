package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteTagEntity
import ch.barrierelos.backend.model.WebsiteTag
import java.sql.Timestamp

public fun WebsiteTag.toEntity(): WebsiteTagEntity
{
  return WebsiteTagEntity(
    websiteTagId = this.id,
    websiteFk = this.websiteId,
    userFk = this.userId,
    tag = this.tag.toEntity(),
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteTagEntity.toModel(): WebsiteTag
{
  return WebsiteTag(
    id = this.websiteTagId,
    websiteId = this.websiteFk,
    userId = this.userFk,
    tag = this.tag.toModel(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteTagEntity.toModel(websiteTag: WebsiteTag): WebsiteTag
{
  return websiteTag.apply {
    id = this@toModel.websiteTagId
    websiteId = this@toModel.websiteFk
    userId = this@toModel.userFk
    tag = this@toModel.tag.toModel(tag)
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<WebsiteTag>.toEntities(): MutableSet<WebsiteTagEntity>
{
  return this.map { tag -> tag.toEntity() }.toMutableSet()
}

public fun Collection<WebsiteTagEntity>.toModels(): MutableSet<WebsiteTag>
{
  return this.map { tag -> tag.toModel() }.toMutableSet()
}
