package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.helper.createReportEntity
import ch.barrierelos.backend.repository.ReportRepository
import ch.barrierelos.backend.repository.ReportRepository.Companion.findAllByUserFk
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class ReportRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var reportRepository: ReportRepository

  @Test
  fun `finds by user fk, when user fk exists`()
  {
    // given
    val userFk = 25L

    // when
    entityManager.persist(createReportEntity(userFk = userFk).apply { reason = ReasonEnum.INCORRECT })
    entityManager.persist(createReportEntity(userFk = userFk).apply { reason = ReasonEnum.MISLEADING })
    entityManager.flush()

    // then
    val actual = reportRepository.findAllByUserFk(userFk).content

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.reason == ReasonEnum.INCORRECT }
    actual.shouldHaveSingleElement { it.reason == ReasonEnum.MISLEADING }
  }

  @Test
  fun `not exists by user fk, when user fk not exists`()
  {
    // when
    entityManager.persist(createReportEntity(userFk = 25))
    entityManager.flush()

    // then
    val reports = reportRepository.findAllByUserFk(5000).content

    reports.shouldBeEmpty()
  }
}