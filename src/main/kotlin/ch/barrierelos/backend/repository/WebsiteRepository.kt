package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteEntity

public interface WebsiteRepository : Repository<WebsiteEntity>
{
  public fun findByDomainLike(domain: String): WebsiteEntity?
}
