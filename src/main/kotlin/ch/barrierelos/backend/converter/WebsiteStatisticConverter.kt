package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebsiteStatisticEntity
import ch.barrierelos.backend.model.WebsiteStatistic
import java.sql.Timestamp

public fun WebsiteStatistic.toEntity(): WebsiteStatisticEntity
{
  return WebsiteStatisticEntity(
    websiteStatisticId = this.id,
    score = this.score,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebsiteStatisticEntity.toModel(): WebsiteStatistic
{
  return WebsiteStatistic(
    id = this.websiteStatisticId,
    score = this.score,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebsiteStatisticEntity.toModel(websiteStatistic: WebsiteStatistic): WebsiteStatistic
{
  return websiteStatistic.apply {
    id = this@toModel.websiteStatisticId
    score = this@toModel.score
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
