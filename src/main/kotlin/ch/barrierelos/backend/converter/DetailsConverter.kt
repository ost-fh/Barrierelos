package ch.barrierelos.backend.converter

import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.entity.WebpageDetailsEntity
import ch.barrierelos.backend.entity.WebsiteDetailsEntity
import ch.barrierelos.backend.model.WebpageDetails
import ch.barrierelos.backend.model.WebsiteDetails


public fun WebsiteDetailsEntity.toModel(webpageDetails: MutableSet<WebpageDetailsEntity>): WebsiteDetails
{
  return WebsiteDetails(
    id = this.websiteDetailsId,
    website = this.website.toModel(),
    statistics = this.statistics?.toModel(),
    scanResult = this.scanResult?.toModel(),
    user = this.user.toModel(),
    webpages = webpageDetails.map { it.toModel() }.toMutableSet(),
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageDetailsEntity.toModel(): WebpageDetails
{
  return WebpageDetails(
    id = this.webpageDetailsId,
    webpage = this.webpage.toModel(),
    statistics = this.statistics?.toModel(),
    scanResult = this.scanResult?.toModel(),
    modified = this.modified.time,
    created = this.created.time,
  )
}
