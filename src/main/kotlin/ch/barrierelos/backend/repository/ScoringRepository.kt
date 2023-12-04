package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.ScoringEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface ScoringRepository : Repository<ScoringEntity>
{
  @Query(
    nativeQuery = true, value = """
        with webpage_counts as (
            select wr.webpage_result_id, sum(
                c.violated_count * CASE
                    WHEN c.impact = 'MINOR' THEN 2.0
                    WHEN c.impact = 'MODERATE' THEN 4.0
                    WHEN c.impact = 'SERIOUS' THEN 8.0
                    WHEN c.impact = 'CRITICAL' THEN 16.0
                END
            ) as summed_weighted_violated_count,
            sum(c.passed_count) as summed_passed_count
            from "check" as c
            inner join rule as r on r.rule_id = c.rule_fk
            inner join webpage_result as wr on wr.webpage_result_id = r.webpage_result_fk
            where wr.website_result_fk = :id
            group by wr.webpage_result_id
        )
        select
            wr.webpage_result_id as scoring_id,
            wr.path,
            100 - webpage_counts.summed_weighted_violated_count / (webpage_counts.summed_weighted_violated_count + webpage_counts.summed_passed_count) * 100 as score,
            webpage_counts.summed_passed_count + webpage_counts.summed_weighted_violated_count as total_count,
            wr.modified,
            wr.created
        from webpage_result as wr
        inner join webpage_counts on webpage_counts.webpage_result_id = wr.webpage_result_id
        order by wr.webpage_result_id
    """
  )
  public fun calculateWebpageScores(@Param("id") websiteResultId: Long): List<ScoringEntity>
}
