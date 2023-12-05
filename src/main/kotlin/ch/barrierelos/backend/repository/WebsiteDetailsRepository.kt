package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteDetailsEntity
import org.springframework.data.jpa.repository.Query

public interface WebsiteDetailsRepository: Repository<WebsiteDetailsEntity>
{
  @Query(
    """
    SELECT new WebsiteDetailsEntity (
        COALESCE(wsc.websiteScanId, 0),
        w,
        wst,
        wr,
        u,
        COALESCE(wsc.modified, NOW()),
        COALESCE(wsc.created, NOW())
    )
    FROM WebsiteEntity w
    LEFT JOIN WebsiteScanEntity wsc ON w.websiteId = wsc.websiteFk
    LEFT JOIN WebsiteStatisticEntity wst ON wst.websiteStatisticId = wsc.websiteStatisticFk
    LEFT JOIN WebsiteResultEntity wr ON wr.websiteResultId = wsc.websiteResultFk
    INNER JOIN UserEntity u ON u.userId = w.userFk
    WHERE w.websiteId = ?1
    ORDER BY wsc.created ASC
    LIMIT 1
  """
  )
  public fun findMostRecentByWebsiteId(websiteId: Long): WebsiteDetailsEntity
}
