package ch.barrierelos.backend.repository

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.ReportMessageEntity
import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.toPageable
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

public interface ReportMessageRepository : Repository<ReportMessageEntity>
{
  public fun findAllByReportFk(reportFk: Long, pageable: Pageable): Page<ReportMessageEntity>

  public fun findAllByUserFk(userFk: Long, pageable: Pageable): Page<ReportMessageEntity>

  @Query("SELECT m.reportFk FROM ReportMessageEntity m WHERE m.userFk = (:userFk)")
  public fun findAllReportIdsFromMessagesWrittenByTheUser(userFk: Long): Set<Long>

  public fun findAllByReportFkIn(reportFks: Collection<Long>): Set<ReportMessageEntity>

  public companion object
  {
    public fun ReportMessageRepository.findAllByReportFk(reportFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<ReportMessage>
    {
      val pageable = defaultParameters.toPageable(ReportMessageEntity::class.java)

      return this.findAllByReportFk(reportFk, pageable).toResult(ReportMessageEntity::toModel)
    }

    public fun ReportMessageRepository.findAllByUserFk(userFk: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<ReportMessage>
    {
      val pageable = defaultParameters.toPageable(ReportMessageEntity::class.java)

      return this.findAllByUserFk(userFk, pageable).toResult(ReportMessageEntity::toModel)
    }
  }
}
