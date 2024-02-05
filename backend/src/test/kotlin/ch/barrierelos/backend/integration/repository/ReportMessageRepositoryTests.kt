package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createReportMessageEntity
import ch.barrierelos.backend.repository.ReportMessageRepository
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByReportFk
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByUserFk
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class ReportMessageRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var reportMessageRepository: ReportMessageRepository

  @Test
  fun `finds by report fk, when report fk exists`()
  {
    // given
    val reportFk = 25L

    // when
    entityManager.persist(createReportMessageEntity(reportFk = reportFk).apply { message = "one" })
    entityManager.persist(createReportMessageEntity(reportFk = reportFk).apply { message = "two" })
    entityManager.flush()

    // then
    val actual = reportMessageRepository.findAllByReportFk(reportFk).content

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.message == "one" }
    actual.shouldHaveSingleElement { it.message == "two" }
  }

  @Test
  fun `not exists by report fk, when report fk not exists`()
  {
    // when
    entityManager.persist(createReportMessageEntity(reportFk = 25))
    entityManager.flush()

    // then
    val reportMessages = reportMessageRepository.findAllByReportFk(5000).content

    reportMessages.shouldBeEmpty()
  }

  @Test
  fun `finds by user fk, when user fk exists`()
  {
    // given
    val userFk = 25L

    // when
    entityManager.persist(createReportMessageEntity(userFk = userFk).apply { message = "one" })
    entityManager.persist(createReportMessageEntity(userFk = userFk).apply { message = "two" })
    entityManager.flush()

    // then
    val actual = reportMessageRepository.findAllByUserFk(userFk).content

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.message == "one" }
    actual.shouldHaveSingleElement { it.message == "two" }
  }

  @Test
  fun `not exists by user fk, when user fk not exists`()
  {
    // when
    entityManager.persist(createReportMessageEntity(userFk = 25))
    entityManager.flush()

    // then
    val reportMessages = reportMessageRepository.findAllByUserFk(5000).content

    reportMessages.shouldBeEmpty()
  }
}