package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageEntity
import ch.barrierelos.backend.model.Webpage
import java.sql.Timestamp

public fun Webpage.toEntity(): WebpageEntity
{
  return WebpageEntity(
    webpageId = this.id,
    websiteFk = this.websiteFk,
    userFk = this.userId,
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageEntity.toModel(): Webpage
{
  return Webpage(
    id = this.webpageId,
    websiteFk = this.websiteFk,
    userId = this.userFk,
    domain = this.domain,
    url = this.url,
    category = this.category,
    status = this.status,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageEntity.toModel(webpage: Webpage): Webpage
{
  return webpage.apply {
    id = this@toModel.webpageId
    websiteFk = this@toModel.websiteFk
    userId = this@toModel.userFk
    domain = this@toModel.domain
    url = this@toModel.url
    category = this@toModel.category
    status = this@toModel.status
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
