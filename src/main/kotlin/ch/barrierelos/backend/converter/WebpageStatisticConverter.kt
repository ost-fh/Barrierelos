package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.WebpageStatisticEntity
import ch.barrierelos.backend.model.WebpageStatistic
import java.sql.Timestamp

public fun WebpageStatistic.toEntity(): WebpageStatisticEntity
{
  return WebpageStatisticEntity(
    webpageStatisticId = this.id,
    webpageFk = this.webpageId,
    userFk = this.userId,
    score = this.score,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun WebpageStatisticEntity.toModel(): WebpageStatistic
{
  return WebpageStatistic(
    id = this.webpageStatisticId,
    webpageId = this.webpageFk,
    userId = this.userFk,
    score = this.score,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun WebpageStatisticEntity.toModel(webpageStatistic: WebpageStatistic): WebpageStatistic
{
  return webpageStatistic.apply {
    id = this@toModel.webpageStatisticId
    webpageId = this@toModel.webpageFk
    userId = this@toModel.userFk
    score = this@toModel.score
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
