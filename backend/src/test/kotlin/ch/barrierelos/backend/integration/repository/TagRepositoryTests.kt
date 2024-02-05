package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createTagEntity
import ch.barrierelos.backend.repository.TagRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class TagRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var tagRepository: TagRepository

  @Test
  fun `finds tags, when tags exist`()
  {
    // when
    entityManager.persist(createTagEntity().apply { name = "one" })
    entityManager.persist(createTagEntity().apply { name = "two" })
    entityManager.persist(createTagEntity().apply { name = "three" })
    entityManager.persist(createTagEntity().apply { name = "four" })
    entityManager.flush()

    // then
    val actual = tagRepository.findAll()

    Assertions.assertNotNull(actual)
    actual.shouldNotBeEmpty()
    actual.shouldHaveSize(4)
  }

  @Test
  fun `cannot find tags, when no tags exist`()
  {
    // then
    val actual = tagRepository.findAll()

    Assertions.assertNotNull(actual)
    actual.shouldBeEmpty()
    actual.shouldHaveSize(0)
  }

  @Test
  fun `find tag by name, when tag exists`()
  {
    // when
    val expected = createTagEntity()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = tagRepository.findByName(expected.name)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.tagId)
      Assertions.assertEquals(expected.tagId, actual.tagId)
      Assertions.assertEquals(expected.name, actual.name)
    }
  }

  @Test
  fun `cannot find tag by name, when tag not exists`()
  {
    // when
    val expected = createTagEntity()

    // then
    val actual = tagRepository.findByName(expected.name)

    Assertions.assertNull(actual)
  }
}