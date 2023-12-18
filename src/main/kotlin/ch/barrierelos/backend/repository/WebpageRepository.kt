package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebpageEntity

public interface WebpageRepository : Repository<WebpageEntity>
{
  public fun existsByDisplayUrl(displayUrl: String): Boolean

  public fun findAllByWebsiteWebsiteId(id: Long): MutableSet<WebpageEntity>
}
