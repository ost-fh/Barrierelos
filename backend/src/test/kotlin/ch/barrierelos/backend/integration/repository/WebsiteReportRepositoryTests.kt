package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.helper.createWebsiteReportEntity
import ch.barrierelos.backend.repository.WebsiteReportRepository
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAllByWebsiteFk
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebsiteReportRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var websiteReportRepository: WebsiteReportRepository

  @Test
  fun `finds by website fk, when website fk exists`()
  {
    // given
    val websiteFk = 25L

    // when
    entityManager.persist(createWebsiteReportEntity(websiteFk = websiteFk).apply { report.reason = ReasonEnum.INCORRECT })
    entityManager.persist(createWebsiteReportEntity(websiteFk = websiteFk).apply { report.reason = ReasonEnum.MISLEADING })
    entityManager.flush()

    // then
    val actual = websiteReportRepository.findAllByWebsiteFk(websiteFk).content

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.INCORRECT }
    actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.MISLEADING }
  }

  @Test
  fun `not exists by website fk, when website fk not exists`()
  {
    // when
    entityManager.persist(createWebsiteReportEntity(websiteFk = 25))
    entityManager.flush()

    // then
    val websiteReports = websiteReportRepository.findAllByWebsiteFk(5000).content

    websiteReports.shouldBeEmpty()
  }
}