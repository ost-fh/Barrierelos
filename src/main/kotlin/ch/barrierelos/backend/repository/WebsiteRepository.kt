package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.entity.WebsiteEntity
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.model.Region
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.repository.Repository.Companion.lastModified
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import java.sql.Timestamp

public interface WebsiteRepository : Repository<WebsiteEntity>
{
  public fun findByDeletedAndCreatedAfterAndModifiedAfter(deleted: Boolean, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebsiteEntity>

  public fun findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(deleted: Boolean, status: StatusEnum, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebsiteEntity>

  public fun findByDomainContainingOrderByDomain(domain: String, pageable: Pageable = Pageable.ofSize(5)): List<WebsiteEntity>

  @Query("""
    SELECT new Region(t.tagId, t.name, (sum(w.score) / (count(w))))
    FROM TagEntity t
    INNER JOIN WebsiteTagEntity wt ON wt.tag = t
    INNER JOIN WebsiteEntity w ON w.websiteId = wt.websiteFk
    WHERE
        (t.name LIKE 'Canton: %' OR t.name LIKE 'Country: %') AND
        w.deleted = false
    GROUP BY t
    ORDER BY sum(w.score) / (count(w)) DESC
  """)
  public fun findAllRegions(pageable: Pageable): Page<Region>

  public fun existsByDomain(domain: String): Boolean

  public fun findAllByWebsiteIdIn(websiteIds: Collection<Long>): Set<WebsiteEntity>

  public companion object
  {
    public fun WebsiteRepository.findAll(showDeleted: Boolean, showBlocked: Boolean, defaultParameters: DefaultParameters): Result<Website>
    {
      val lastModified = this.lastModified()

      val modifiedAfter = Timestamp(defaultParameters.modifiedAfter ?: 0)
      val createdAfter = Timestamp(defaultParameters.createdAfter ?: 0)

      val pageable = defaultParameters.toPageable(WebsiteEntity::class.java)

      return if(showBlocked)
      {
        this.findByDeletedAndCreatedAfterAndModifiedAfter(showDeleted, createdAfter, modifiedAfter, pageable).toResult(lastModified, WebsiteEntity::toModel)
      }
      else
      {
        this.findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(showDeleted, StatusEnum.BLOCKED, createdAfter, modifiedAfter, pageable).toResult(lastModified, WebsiteEntity::toModel)
      }
    }
  }
}
