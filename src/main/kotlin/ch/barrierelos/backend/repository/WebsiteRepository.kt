package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebsiteEntity
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.lastModified
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.sql.Timestamp

public interface WebsiteRepository : Repository<WebsiteEntity>
{
  public fun findByDeletedAndCreatedAfterAndModifiedAfter(deleted: Boolean, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebsiteEntity>

  public fun findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(deleted: Boolean, status: StatusEnum, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebsiteEntity>

  public fun findByDomainContainingOrderByDomain(domain: String, pageable: Pageable = Pageable.ofSize(5)): List<WebsiteEntity>

  public fun existsByDomain(domain: String): Boolean

  public companion object
  {
    public fun WebsiteRepository.findAll(deleted: Boolean, blocked: Boolean, defaultParameters: DefaultParameters): Result<Website>
    {
      val count = this.count()
      val lastModified = this.lastModified()

      val modifiedAfter = Timestamp(defaultParameters.modifiedAfter ?: 0)
      val createdAfter = Timestamp(defaultParameters.createdAfter ?: 0)

      val pageable = defaultParameters.toPageable(count, WebsiteEntity::class.java)

      return if(blocked)
      {
        this.findByDeletedAndCreatedAfterAndModifiedAfter(deleted, createdAfter, modifiedAfter, pageable).toResult(count, lastModified, WebsiteEntity::toModel)
      }
      else
      {
        this.findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(deleted, StatusEnum.BLOCKED, createdAfter, modifiedAfter, pageable).toResult(count, lastModified, WebsiteEntity::toModel)
      }
    }
  }
}
