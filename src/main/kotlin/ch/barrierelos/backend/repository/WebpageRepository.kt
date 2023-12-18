package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebpageEntity
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.lastModified
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.sql.Timestamp

public interface WebpageRepository : Repository<WebpageEntity>
{
  public fun existsByDisplayUrl(displayUrl: String): Boolean

  public fun findAllByWebsiteWebsiteId(id: Long): MutableSet<WebpageEntity>

  public fun findByDeletedAndCreatedAfterAndModifiedAfter(deleted: Boolean, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebpageEntity>

  public fun findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(deleted: Boolean, status: StatusEnum, created: Timestamp, modified: Timestamp, pageable: Pageable): Page<WebpageEntity>

  public fun existsByPathAndWebsiteFk(path: String, websiteFk: Long): Boolean

  public companion object
  {
    public fun WebpageRepository.findAll(deleted: Boolean, blocked: Boolean, defaultParameters: DefaultParameters): Result<Webpage>
    {
      val count = this.count()
      val lastModified = this.lastModified()

      val modifiedAfter = Timestamp(defaultParameters.modifiedAfter ?: 0)
      val createdAfter = Timestamp(defaultParameters.createdAfter ?: 0)

      val pageable = defaultParameters.toPageable(count, WebpageEntity::class.java)

      return if(blocked)
      {
        this.findByDeletedAndCreatedAfterAndModifiedAfter(deleted, createdAfter, modifiedAfter, pageable).toResult(count, lastModified, WebpageEntity::toModel)
      }
      else
      {
        this.findByDeletedAndStatusNotAndCreatedAfterAndModifiedAfter(deleted, StatusEnum.BLOCKED, createdAfter, modifiedAfter, pageable).toResult(count, lastModified, WebpageEntity::toModel)
      }
    }
  }
}
