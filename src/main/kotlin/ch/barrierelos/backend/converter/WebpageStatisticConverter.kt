package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageStatisticEntity
import ch.barrierelos.backend.model.WebpageStatistic
import java.sql.Timestamp

public fun WebpageStatistic.toEntity(): WebpageStatisticEntity
{
  return WebpageStatisticEntity(
    webpageStatisticId = this.id,
    score = this.score,
    weight = this.weight,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageStatisticEntity.toModel(): WebpageStatistic
{
  return WebpageStatistic(
    id = this.webpageStatisticId,
    score = this.score,
    weight = this.weight,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageStatisticEntity.toModel(webpageStatistic: WebpageStatistic): WebpageStatistic
{
  return webpageStatistic.apply {
    id = this@toModel.webpageStatisticId
    score = this@toModel.score
    weight = this@toModel.weight
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
