package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteEntity
import ch.barrierelos.backend.message.WebsiteMessage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.WebsiteTag
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.clearAndAddAll
import java.sql.Timestamp


public fun WebsiteMessage.toModel(domain: String, tags: MutableSet<WebsiteTag>): Website
{
  return Website(
    user = Security.getUser(),
    domain = domain,
    url = this.url,
    category = this.category,
    tags = tags,
  )
}

public fun Website.toEntity(): WebsiteEntity
{
  return WebsiteEntity(
    websiteId = this.id,
    user = this.user.toEntity(),
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    score = this.score,
    tags = this.tags.toEntities(),
    deleted = this.deleted,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteEntity.toModel(): Website
{
  return Website(
    id = this.websiteId,
    user = this.user.toModel(),
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    score = this.score,
    tags = this.tags.toModels(),
    deleted = this.deleted,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteEntity.toModel(website: Website): Website
{
  return website.apply {
    id = this@toModel.websiteId
    user = this@toModel.user.toModel()
    domain = this@toModel.domain
    url = this@toModel.url
    category = this@toModel.category
    status = this@toModel.status
    score = this@toModel.score
    tags.clearAndAddAll(this@toModel.tags.toModels())
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
