package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createWebpageScanEntity
import ch.barrierelos.backend.repository.WebpageScanRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebpageScanRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var webpageScanRepository: WebpageScanRepository

  @Test
  fun `finds by webpage fk, when webpage fk exists`()
  {
    // given
    val webpageFk = 25L

    // when
    entityManager.persist(createWebpageScanEntity(webpage = webpageFk, webpageStatistic = 1))
    entityManager.persist(createWebpageScanEntity(webpage = webpageFk, webpageStatistic = 2))
    entityManager.flush()

    // then
    val actual = webpageScanRepository.findAllByWebpageFk(webpageFk)

    actual.shouldHaveSize(2)
    actual.shouldHaveSingleElement { it.webpageStatisticFk == 1L }
    actual.shouldHaveSingleElement { it.webpageStatisticFk == 2L }
  }

  @Test
  fun `not exists by webpage fk, when webpage fk not exists`()
  {
    // when
    entityManager.persist(createWebpageScanEntity(webpage = 25))
    entityManager.flush()

    // then
    val webpageScans = webpageScanRepository.findAllByWebpageFk(5000)

    webpageScans.shouldBeEmpty()
  }
}