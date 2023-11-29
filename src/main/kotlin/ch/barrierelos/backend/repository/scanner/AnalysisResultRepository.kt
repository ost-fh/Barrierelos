package ch.barrierelos.backend.repository.scanner

import ch.barrierelos.backend.entity.scanner.AnalysisResultEntity
import ch.barrierelos.backend.repository.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface AnalysisResultRepository : Repository<AnalysisResultEntity>
{
  @Modifying
  @Query("DELETE FROM AnalysisResultEntity a WHERE a.analysisJob.analysisJobId = :jobFk")
  public fun deleteByAnalysisJobFk(@Param("jobFk") analysisJobFk: Long)
}
