package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.repository.WebpageScanRepository
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebpageScanRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var webpageScanRepository: WebpageScanRepository

  // TODO: Add tests for WebpageScanRepository.findAllByWebsiteScanWebsiteScanId()

  //  @Test
  //  fun `finds by webpage fk, when webpage fk exists`()
  //  {
  //    // given
  //    val webpageFk = 25L
  //
  //    // when
  //    entityManager.persist(createWebpageScanEntity().apply {
  //      webpage.webpageId = webpageFk
  //      webpageStatistic!!.webpageStatisticId = 1
  //    })
  //    entityManager.persist(createWebpageScanEntity().apply {
  //      webpage.webpageId = webpageFk
  //      webpageStatistic!!.webpageStatisticId = 2
  //    })
  //    entityManager.flush()
  //
  //    // then
  //    val actual = webpageScanRepository.findAllByWebsiteScanWebsiteScanId(webpageFk)
  //
  //    actual.shouldHaveSize(2)
  //    actual.shouldHaveSingleElement { it.webpageStatistic!!.webpageStatisticId == 1L }
  //    actual.shouldHaveSingleElement { it.webpageStatistic!!.webpageStatisticId == 2L }
  //  }
  //
  //  @Test
  //  fun `not exists by webpage fk, when webpage fk not exists`()
  //  {
  //    // when
  //    entityManager.persist(createWebpageScanEntity(webpage = 25))
  //    entityManager.flush()
  //
  //    // then
  //    val webpageScans = webpageScanRepository.findAllByWebpageFk(5000)
  //
  //    webpageScans.shouldBeEmpty()
  //  }
}
