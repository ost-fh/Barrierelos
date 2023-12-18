package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteScanEntity
import java.util.*

public interface WebsiteScanRepository : Repository<WebsiteScanEntity>
{
  public fun findFirstByWebsiteWebsiteIdOrderByCreated(websiteId: Long): Optional<WebsiteScanEntity>
}
