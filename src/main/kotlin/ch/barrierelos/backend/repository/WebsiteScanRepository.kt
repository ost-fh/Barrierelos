package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteScanEntity
import org.springframework.data.jpa.repository.Query

public interface WebsiteScanRepository : Repository<WebsiteScanEntity>
{
  @Query("SELECT ws FROM WebsiteScanEntity ws WHERE ws.websiteFk = ?1 ORDER BY ws.created DESC LIMIT 1")
  public fun findLatestByWebsiteFk(websiteFk: Long): WebsiteScanEntity?
}
