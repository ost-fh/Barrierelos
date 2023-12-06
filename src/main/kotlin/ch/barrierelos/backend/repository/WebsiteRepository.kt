package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.WebsiteEntity

public interface WebsiteRepository : Repository<WebsiteEntity>
{
  public fun findByDomainContaining(domain: String): Set<WebsiteEntity>

  public fun existsByDomain(domain: String): Boolean
}
