package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebpageScanEntity

public interface WebpageScanRepository : Repository<WebpageScanEntity>
{
  public fun findAllByWebsiteScanWebsiteScanId(websiteScanId: Long): MutableSet<WebpageScanEntity>
}
