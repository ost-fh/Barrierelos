package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createWebpageEntity
import ch.barrierelos.backend.helper.createWebsiteEntity
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
  fun `exists by displayUrl, when displayUrl exists`()
  {
    // when
    val user = createUserEntity()
    val website = createWebsiteEntity(user)
    website.tags.clear()
    val webpage = createWebpageEntity(user)
    webpage.website = website

    entityManager.persist(user)
    entityManager.persist(website)
    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByDisplayUrl(webpage.displayUrl)

    Assertions.assertTrue(exists)
  }

  @Test
  fun `exists by displayUrl, when displayUrl not exists`()
  {
    // when
    val user = createUserEntity()
    val website = createWebsiteEntity(user)
    website.tags.clear()
    val webpage = createWebpageEntity(user)
    webpage.website = website
    val expected = webpage.displayUrl
    webpage.displayUrl = "test.$expected"

    entityManager.persist(user)
    entityManager.persist(website)
    entityManager.persist(webpage)
    entityManager.flush()

    // then
    val exists = webpageRepository.existsByDisplayUrl(expected)

    Assertions.assertFalse(exists)
  }
}