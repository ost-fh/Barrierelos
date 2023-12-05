package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebpageDetailsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

public interface WebpageDetailsRepository : Repository<WebpageDetailsEntity>
{
  @Query(
    """
    SELECT new WebpageDetailsEntity (
        COALESCE(wsc.webpageScanId, 0),
        COALESCE(wsc.websiteScanFk, 0),
        w,
        wst,
        wr,
        COALESCE(wsc.modified, NOW()),
        COALESCE(wsc.created, NOW())
    )
    FROM WebpageEntity w
    LEFT JOIN WebpageScanEntity wsc ON w.webpageId = wsc.webpageFk
    LEFT JOIN WebpageStatisticEntity wst ON wst.webpageStatisticId = wsc.webpageStatisticFk
    LEFT JOIN WebpageResultEntity wr ON wr.webpageResultId = wsc.webpageResultFk
    WHERE w.websiteFk = ?1
    ORDER BY wsc.created ASC
  """
  )
  public fun findMostRecentByWebsiteId(websiteId: Long, pageable: Pageable): List<WebpageDetailsEntity>
}
