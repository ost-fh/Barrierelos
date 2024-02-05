package ch.barrierelos.backend.repository.scanner

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import ch.barrierelos.backend.repository.Repository

public interface WebpageResultRepository : Repository<WebpageResultEntity>
{
  public fun findAllByWebsiteResultWebsiteResultId(websiteResultId: Long): MutableSet<WebpageResultEntity>
}
