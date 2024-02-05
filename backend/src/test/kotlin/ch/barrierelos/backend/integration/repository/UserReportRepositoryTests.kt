package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.helper.createUserReportEntity
import ch.barrierelos.backend.repository.UserReportRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class UserReportRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var userReportRepository: UserReportRepository

  @Test
  fun `finds by user fk, when user fk exists`()
  {
    // given
    val userFk = 25L

    // when
    entityManager.persist(createUserReportEntity(userFk = userFk).apply { report.reason = ReasonEnum.INCORRECT })
    entityManager.persist(createUserReportEntity(userFk = userFk).apply { report.reason = ReasonEnum.MISLEADING })
    entityManager.flush()

    // then
    val actual = userReportRepository.findAllByUserFk(userFk)

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.INCORRECT }
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.MISLEADING }
  }

  @Test
  fun `not exists by user fk, when user fk not exists`()
  {
    // when
    entityManager.persist(createUserReportEntity(userFk = 25))
    entityManager.flush()

    // then
    val userReports = userReportRepository.findAllByUserFk(5000)

    userReports.shouldBeEmpty()
  }
}