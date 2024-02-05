package ch.barrierelos.backend.repository.scanner

import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import ch.barrierelos.backend.repository.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface WebsiteResultRepository : Repository<WebsiteResultEntity>
{
  @Modifying
  @Query("DELETE FROM WebsiteResultEntity a WHERE a.scanJob.scanJobId = :jobFk")
  public fun deleteByScanJobFk(@Param("jobFk") scanJobFk: Long)
}
