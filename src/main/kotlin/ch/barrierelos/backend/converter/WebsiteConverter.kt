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
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
