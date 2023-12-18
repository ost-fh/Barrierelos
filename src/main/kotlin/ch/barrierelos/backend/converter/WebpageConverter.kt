package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageEntity
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.message.WebpageMessage
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.security.Security
import java.sql.Timestamp


public fun WebpageMessage.toModel(website: Website, displayUrl: String): Webpage
{
  return Webpage(
    user = Security.getUser(),
    website = website,
    displayUrl = displayUrl,
    url = this.url,
    status = StatusEnum.PENDING_INITIAL,
  )
}

public fun Webpage.toEntity(): WebpageEntity
{
  return WebpageEntity(
    webpageId = this.id,
    website = this.website.toEntity(),
    user = this.user.toEntity(),
    displayUrl = this.displayUrl,
    url = this.url,
    status = this.status,
    deleted = this.deleted,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageEntity.toModel(): Webpage
{
  return Webpage(
    id = this.webpageId,
    website = this.website.toModel(),
    user = this.user.toModel(),
    displayUrl = this.displayUrl,
    url = this.url,
    status = this.status,
    deleted = this.deleted,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageEntity.toModel(webpage: Webpage): Webpage
{
  return webpage.apply {
    id = this@toModel.webpageId
    website = this@toModel.website.toModel()
    user = this@toModel.user.toModel()
    displayUrl = this@toModel.displayUrl
    url = this@toModel.url
    status = this@toModel.status
    deleted = this@toModel.deleted
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}

public fun Collection<Webpage>.toEntities(): MutableSet<WebpageEntity>
{
  return this.map { webpage -> webpage.toEntity() }.toMutableSet()
}

public fun Collection<WebpageEntity>.toModels(): MutableSet<Webpage>
{
  return this.map { webpage -> webpage.toModel() }.toMutableSet()
}
