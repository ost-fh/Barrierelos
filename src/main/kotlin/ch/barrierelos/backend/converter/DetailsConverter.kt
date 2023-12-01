package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageDetailsEntity
import ch.barrierelos.backend.entity.WebsiteDetailsEntity
import ch.barrierelos.backend.model.WebpageDetails
import ch.barrierelos.backend.model.WebsiteDetails
import java.sql.Timestamp

public fun WebsiteDetails.toEntity(): WebsiteDetailsEntity
{
  val websiteDetails = WebsiteDetailsEntity(
    websiteDetailsId = this.id,
    score = this.score,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
  websiteDetails.webpages = this.webpages.map { it.toEntity(websiteDetails) }.toMutableSet()
  return websiteDetails
}

public fun WebsiteDetailsEntity.toModel(): WebsiteDetails
{
  return WebsiteDetails(
    id = this.websiteDetailsId,
    score = this.score,
    webpages = this.webpages.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageDetails.toEntity(websiteDetails: WebsiteDetailsEntity): WebpageDetailsEntity
{
  return WebpageDetailsEntity(
    webpageDetailsId = this.id,
    path = this.path,
    score = this.score,
    website = websiteDetails,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageDetailsEntity.toModel(): WebpageDetails
{
  return WebpageDetails(
    id = this.webpageDetailsId,
    path = this.path,
    score = this.score,
    modified = this.modified.time,
    created = this.created.time,
  )
}
