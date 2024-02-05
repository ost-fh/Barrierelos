package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.entity.ReportEntity
import ch.barrierelos.backend.model.Report
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

public interface ReportRepository : Repository<ReportEntity>
{
  public fun findAllByUserFk(userFk: Long, pageable: Pageable): Page<ReportEntity>

  public companion object
  {
    public fun ReportRepository.findAllByUserFk(userFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<Report>
    {
      val pageable = defaultParameters.toPageable(ReportEntity::class.java)

      return this.findAllByUserFk(userFk, pageable).toResult(ReportEntity::toModel)
    }
  }
}
