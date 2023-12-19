package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.ReportEntity

public interface ReportRepository : Repository<ReportEntity>
{
  public fun findAllByUserFk(userFk: Long): Set<ReportEntity>
}
