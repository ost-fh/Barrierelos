package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebpageEntity

public interface WebpageRepository : Repository<WebpageEntity>
{
  public fun existsByPathAndWebsiteFk(path: String, websiteFk: Long): Boolean
}
