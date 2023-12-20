package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createWebsiteScanEntity
import ch.barrierelos.backend.repository.WebsiteScanRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebsiteScanRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var websiteScanRepository: WebsiteScanRepository

  @Test
  fun `finds by website fk, when website fk exists`()
  {
    // given
    val websiteFk = 25L

    // when
    entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk, websiteStatisticFk = 1))
    entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk, websiteStatisticFk = 2))
    entityManager.flush()

    // then
    val actual = websiteScanRepository.findAllByWebsiteFk(websiteFk)

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.websiteStatisticFk == 1L }
    actual.shouldHaveSingleElement { it.websiteStatisticFk == 2L }
  }

  @Test
  fun `not exists by website fk, when website fk not exists`()
  {
    // when
    entityManager.persist(createWebsiteScanEntity(websiteFk = 25))
    entityManager.flush()

    // then
    val websiteScans = websiteScanRepository.findAllByWebsiteFk(5000)

    websiteScans.shouldBeEmpty()
  }
}