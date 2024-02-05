package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.entity.WebpageReportEntity
import ch.barrierelos.backend.model.WebpageReport
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

public interface WebpageReportRepository : JpaRepository<WebpageReportEntity, Long>
{
  public fun findAllByWebpageFk(webpageFk: Long, pageable: Pageable): Page<WebpageReportEntity>

  @Query("SELECT r FROM WebpageReportEntity r WHERE r.report.reportId IN (:reportFks)")
  public fun findAllByReportFks(reportFks: Collection<Long>): Set<WebpageReportEntity>

  public companion object
  {
    public fun WebpageReportRepository.findAll(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageReport>
    {
      val pageable = defaultParameters.toPageable(WebpageReportEntity::class.java)

      return this.findAll(pageable).toResult(WebpageReportEntity::toModel)
    }

    public fun WebpageReportRepository.findAllByWebpageFk(webpageFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<WebpageReport>
    {
      val pageable = defaultParameters.toPageable(WebpageReportEntity::class.java)

      return this.findAllByWebpageFk(webpageFk, pageable).toResult(WebpageReportEntity::toModel)
    }
  }
}
