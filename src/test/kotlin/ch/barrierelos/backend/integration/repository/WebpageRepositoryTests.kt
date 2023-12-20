package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createWebpageEntity
import ch.barrierelos.backend.repository.WebpageRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebpageRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var webpageRepository: WebpageRepository

  @Test
  fun `exists by path and website fk, when path and website fk exists`()
  {
    // when
    val webpage = createWebpageEntity()
    webpage.websiteFk = 25
    webpage.path = "/path"

    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByPathAndWebsiteFk(webpage.path, webpage.websiteFk)

    Assertions.assertTrue(exists)
  }

  @Test
  fun `not exists by path and website fk, when path not exists`()
  {
    // when
    val webpage = createWebpageEntity()
    webpage.websiteFk = 25
    webpage.path = "/path"

    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByPathAndWebsiteFk("/test", webpage.websiteFk)

    Assertions.assertFalse(exists)
  }

  @Test
  fun `not exists by path and website fk, when website fk not exists`()
  {
    // when
    val webpage = createWebpageEntity()
    webpage.websiteFk = 25
    webpage.path = "/path"

    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByPathAndWebsiteFk(webpage.path, 5000)

    Assertions.assertFalse(exists)
  }

  @Test
  fun `not exists by path and website fk, when path and website fk not exists`()
  {
    // when
    val webpage = createWebpageEntity()
    webpage.websiteFk = 25
    webpage.path = "/path"

    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByPathAndWebsiteFk("/test", 5000)

    Assertions.assertFalse(exists)
  }
}