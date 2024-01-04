package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.entity.UserReportEntity
import ch.barrierelos.backend.model.UserReport
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

public interface UserReportRepository : JpaRepository<UserReportEntity, Long>
{
  public fun findAllByUserFk(userFk: Long, pageable: Pageable): Page<UserReportEntity>

  @Query("SELECT r FROM UserReportEntity r WHERE r.report.reportId IN (:reportFks)")
  public fun findAllByReportFks(reportFks: Collection<Long>): Set<UserReportEntity>

  public companion object
  {
    public fun UserReportRepository.findAll(defaultParameters: DefaultParameters = DefaultParameters()): Result<UserReport>
    {
      val pageable = defaultParameters.toPageable(UserReportEntity::class.java)

      return this.findAll(pageable).toResult(UserReportEntity::toModel)
    }

    public fun UserReportRepository.findAllByUserFk(userFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<UserReport>
    {
      val pageable = defaultParameters.toPageable(UserReportEntity::class.java)

      return this.findAllByUserFk(userFk, pageable).toResult(UserReportEntity::toModel)
    }
  }
}
