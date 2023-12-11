package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteEntity
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.util.clearAndAddAll
import java.sql.Timestamp

public fun Website.toEntity(): WebsiteEntity
{
  return WebsiteEntity(
    websiteId = this.id,
    userFk = this.userId,
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    tags = this.tags.toEntities(),
    webpageCount = this.webpageCount,
    deleted = this.deleted,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteEntity.toModel(): Website
{
  return Website(
    id = this.websiteId,
    userId = this.userFk,
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    tags = this.tags.toModels(),
    webpageCount = this.webpageCount,
    deleted = this.deleted,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteEntity.toModel(website: Website): Website
{
  return website.apply {
    id = this@toModel.websiteId
    userId = this@toModel.userFk
    domain = this@toModel.domain
    url = this@toModel.url
    category = this@toModel.category
    status = this@toModel.status
    tags.clearAndAddAll(this@toModel.tags.toModels())
    webpageCount = this@toModel.webpageCount
    deleted = this@toModel.deleted
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<Website>.toEntities(): MutableSet<WebsiteEntity>
{
  return this.map { website -> website.toEntity() }.toMutableSet()
}

public fun Collection<WebsiteEntity>.toModels(): MutableSet<Website>
{
  return this.map { website -> website.toModel() }.toMutableSet()
}
