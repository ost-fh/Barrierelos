package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebsiteReportEntity
import ch.barrierelos.backend.model.WebsiteReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

public interface WebsiteReportRepository : JpaRepository<WebsiteReportEntity, Long>
{
  public fun findAllByWebsiteFk(websiteFk: Long, pageable: Pageable): Page<WebsiteReportEntity>

  @Query("SELECT r FROM WebsiteReportEntity r WHERE r.report.reportId IN (:reportFks)")
  public fun findAllByReportFks(reportFks: Collection<Long>): Set<WebsiteReportEntity>

  public companion object
  {
    public fun WebsiteReportRepository.findAll(defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteReport>
    {
      val pageable = defaultParameters.toPageable(WebsiteReportEntity::class.java)

      return this.findAll(pageable).toResult(WebsiteReportEntity::toModel)
    }

    public fun WebsiteReportRepository.findAllByWebsiteFk(websiteFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<WebsiteReport>
    {
      val pageable = defaultParameters.toPageable(WebsiteReportEntity::class.java)

      return this.findAllByWebsiteFk(websiteFk, pageable).toResult(WebsiteReportEntity::toModel)
    }
  }
}
