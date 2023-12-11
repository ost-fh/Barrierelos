package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageEntity
import ch.barrierelos.backend.model.Webpage
import java.sql.Timestamp

public fun Webpage.toEntity(): WebpageEntity
{
  return WebpageEntity(
    webpageId = this.id,
    websiteFk = this.websiteId,
    userFk = this.userId,
    path = this.path,
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
    websiteId = this.websiteFk,
    userId = this.userFk,
    path = this.path,
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
    websiteId = this@toModel.websiteFk
    userId = this@toModel.userFk
    path = this@toModel.path
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
