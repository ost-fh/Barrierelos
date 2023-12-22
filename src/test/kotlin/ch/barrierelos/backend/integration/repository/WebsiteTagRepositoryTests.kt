package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createWebsiteEntity
import ch.barrierelos.backend.helper.createWebsiteTagEntity
import ch.barrierelos.backend.repository.WebsiteTagRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class WebsiteTagRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var websiteTagRepository: WebsiteTagRepository

  @Test
  fun `finds websiteTag by id, when websiteTag exists`()
  {
    // given
    val user = entityManager.persist(createUserEntity())

    val website = createWebsiteEntity()
    website.user = user
    website.tags.clear()

    entityManager.persist(website)

    // when
    val expected = createWebsiteTagEntity()
    expected.userFk = user.userId
    expected.websiteFk = website.websiteId

    entityManager.persist(expected.tag)
    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = websiteTagRepository.findById(expected.websiteTagId).orElse(null)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.websiteTagId)
      Assertions.assertEquals(expected.websiteTagId, actual.websiteTagId)
      Assertions.assertEquals(expected.userFk, actual.userFk)
      Assertions.assertEquals(expected.tag.tagId, actual.tag.tagId)
      Assertions.assertEquals(expected.tag.name, actual.tag.name)
      Assertions.assertEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }
  }

  @Test
  fun `cannot find websiteTag by id, when websiteTag not exists`()
  {
    // when
    val expected = createWebsiteTagEntity()

    // then
    val actual = websiteTagRepository.findById(expected.websiteTagId).orElse(null)

    Assertions.assertNull(actual)
  }
}