package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.helper.createWebpageReportEntity
import ch.barrierelos.backend.repository.WebpageReportRepository
import ch.barrierelos.backend.repository.WebpageReportRepository.Companion.findAllByWebpageFk
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebpageReportRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var webpageReportRepository: WebpageReportRepository

  @Test
  fun `finds by webpage fk, when webpage fk exists`()
  {
    // given
    val webpageFk = 25L

    // when
    entityManager.persist(createWebpageReportEntity(webpageFk = webpageFk).apply { report.reason = ReasonEnum.INCORRECT })
    entityManager.persist(createWebpageReportEntity(webpageFk = webpageFk).apply { report.reason = ReasonEnum.MISLEADING })
    entityManager.flush()

    // then
    val actual = webpageReportRepository.findAllByWebpageFk(webpageFk).content

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.INCORRECT }
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.MISLEADING }
  }

  @Test
  fun `not exists by webpage fk, when webpage fk not exists`()
  {
    // when
    entityManager.persist(createWebpageReportEntity(webpageFk = 25))
    entityManager.flush()

    // then
    val webpageReports = webpageReportRepository.findAllByWebpageFk(5000).content

    webpageReports.shouldBeEmpty()
  }
}