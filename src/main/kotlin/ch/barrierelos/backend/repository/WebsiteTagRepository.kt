package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteTagEntity
import org.springframework.transaction.annotation.Transactional

public interface WebsiteTagRepository : Repository<WebsiteTagEntity>
{
  @Transactional
  public fun deleteAllByWebsiteFk(websiteFk: Long)
}
