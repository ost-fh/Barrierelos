package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createWebsiteEntity
import ch.barrierelos.backend.repository.WebsiteRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebsiteRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var websiteRepository: WebsiteRepository

  @Test
  fun `finds website by id, when website exists`()
  {
    // given
    val user = entityManager.persist(createUserEntity())

    // when
    val expected = createWebsiteEntity()
    expected.user = user
    expected.tags.clear()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = websiteRepository.findById(expected.websiteId).orElse(null)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.websiteId)
      Assertions.assertEquals(expected.websiteId, actual.websiteId)
      Assertions.assertEquals(expected.user, actual.user)
      Assertions.assertEquals(expected.domain, actual.domain)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.category, actual.category)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.tags.size, actual.tags.size)
      Assertions.assertEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }
  }

  @Test
  fun `cannot find website by id, when website not exists`()
  {
    // when
    val expected = createWebsiteEntity()

    // then
    val actual = websiteRepository.findById(expected.websiteId).orElse(null)

    Assertions.assertNull(actual)
  }

  @Test
  fun `finds website by domain, when website exists`()
  {
    // given
    val user = entityManager.persist(createUserEntity())

    // when
    val expected = createWebsiteEntity()
    expected.user = user
    expected.tags.clear()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = websiteRepository.findByDomainContaining(expected.domain)

    Assertions.assertNotNull(actual)
    actual.shouldNotBeEmpty()
    actual.shouldHaveSize(1)
    actual.first()

    Assertions.assertNotEquals(0, actual.first().websiteId)
    Assertions.assertEquals(expected.websiteId, actual.first().websiteId)
    Assertions.assertEquals(expected.user, actual.first().user)
    Assertions.assertEquals(expected.domain, actual.first().domain)
    Assertions.assertEquals(expected.url, actual.first().url)
    Assertions.assertEquals(expected.category, actual.first().category)
    Assertions.assertEquals(expected.status, actual.first().status)
    Assertions.assertEquals(expected.tags.size, actual.first().tags.size)
    Assertions.assertEquals(expected.modified, actual.first().modified)
    Assertions.assertEquals(expected.created, actual.first().created)
  }

  @Test
  fun `cannot find website by domain, when website not exists`()
  {
    // when
    val expected = createWebsiteEntity()

    // then
    val actual = websiteRepository.findByDomainContaining(expected.domain)

    Assertions.assertNotNull(actual)
    actual.shouldBeEmpty()
    actual.shouldHaveSize(0)
  }
}