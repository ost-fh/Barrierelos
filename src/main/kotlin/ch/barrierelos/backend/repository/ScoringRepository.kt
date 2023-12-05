package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.ScoringEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface ScoringRepository : Repository<ScoringEntity>
{
  @Query(
    nativeQuery = true, value = """
        WITH "webpage_counts" AS (
            SELECT "wr"."webpage_result_id", SUM(
                "c"."violated_count" * CASE
                    WHEN "c"."impact" = 'MINOR' THEN 2.0
                    WHEN "c"."impact" = 'MODERATE' THEN 4.0
                    WHEN "c"."impact" = 'SERIOUS' THEN 8.0
                    WHEN "c"."impact" = 'CRITICAL' THEN 16.0
                END
            ) AS "summed_weighted_violated_count",
            SUM("c"."passed_count") AS "summed_passed_count"
            FROM "check" AS "c"
            INNER JOIN "rule" AS "r" ON "r"."rule_id" = "c"."rule_fk"
            INNER JOIN "webpage_result" AS "wr" ON "wr"."webpage_result_id" = "r"."webpage_result_fk"
            WHERE "wr"."website_result_fk" = :id
            GROUP BY "wr"."webpage_result_id"
        )
        SELECT
            "wr"."webpage_result_id" AS "scoring_id",
            "wr"."path",
            100 - "webpage_counts"."summed_weighted_violated_count" / ("webpage_counts"."summed_weighted_violated_count" + "webpage_counts"."summed_passed_count") * 100 AS "score",
            "webpage_counts"."summed_passed_count" + "webpage_counts"."summed_weighted_violated_count" AS "total_count",
            "wr"."modified",
            "wr"."created"
        FROM "webpage_result" AS "wr"
        INNER JOIN "webpage_counts" ON "webpage_counts"."webpage_result_id" = "wr"."webpage_result_id"
        ORDER BY "wr"."webpage_result_id"
    """
  )
  public fun calculateWebpageScores(@Param("id") websiteResultId: Long): List<ScoringEntity>
}
