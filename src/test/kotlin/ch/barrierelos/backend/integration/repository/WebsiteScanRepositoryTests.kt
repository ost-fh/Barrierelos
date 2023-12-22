package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createWebsiteEntity
import ch.barrierelos.backend.helper.createWebsiteScanEntity
import ch.barrierelos.backend.repository.WebsiteScanRepository
import io.kotest.matchers.optional.shouldBeEmpty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebsiteScanRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var websiteScanRepository: WebsiteScanRepository

  // TODO: Add tests for WebpageScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated()

//  @Test
//  fun `finds by website fk, when website fk exists`()
//  {
//    // given
//    val websiteFk = 25L
//    val user = entityManager.persist(createUserEntity())
//    entityManager.flush()
//    entityManager.persist(createWebsiteEntity(user).apply {
//      websiteId = websiteFk
//      tags.clear()
//    })
//
//    // when
//    val expected = entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk).apply { websiteStatistic!!.score  = 40.0 })
//    entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk).apply { websiteStatistic!!.score  = 60.0 })
//    entityManager.flush()
//
//    // then
//    val actual = websiteScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated(websiteFk)
//
//    Assertions.assertEquals(expected, actual)
//  }
//
//  @Test
//  fun `not exists by website fk, when website fk not exists`()
//  {
//    // given
//    val user = entityManager.persist(createUserEntity())
//    val website = entityManager.persist(createWebsiteEntity(user))
//
//    // when
//    entityManager.persist(createWebsiteScanEntity(websiteFk = 25).apply { this.website = website })
//    entityManager.flush()
//
//    // then
//    val websiteScans = websiteScanRepository.findFirstByWebsiteWebsiteIdOrderByCreated(5000)
//
//    websiteScans.shouldBeEmpty()
//  }
}
