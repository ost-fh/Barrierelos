package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createTagEntity
import ch.barrierelos.backend.helper.createTagModel
import ch.barrierelos.backend.service.TagService
import io.kotest.matchers.collections.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class TagServiceTests : ServiceTests()
{
  @Autowired
  lateinit var tagService: TagService

  @Nested
  @DisplayName("Add Tag")
  inner class AddTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds tag, given admin account`()
    {
      // when
      val expected = createTagModel()

      // then
      val actual = tagService.addTag(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.name, actual.name)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add tag, given wrong account`()
    {
      // when
      val expected = createTagModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.addTag(expected)
      }
    }

    @Test
    fun `cannot add tag, given no account`()
    {
      // when
      val expected = createTagModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.addTag(expected)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add tag, when name already exists`()
    {
      // when
      tagService.addTag(createTagModel())

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        tagService.addTag(createTagModel())
      }
    }
  }

  @Nested
  @DisplayName("Update Tag")
  inner class UpdateTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates tag, given admin account`()
    {
      // when
      val expected = tagService.addTag(createTagModel())
      expected.name = "new"

      // then
      val actual = tagService.updateTag(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.name, actual.name)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update tag, given wrong account`()
    {
      // when
      val expected = tagRepository.save(createTagEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.updateTag(expected)
      }
    }

    @Test
    fun `cannot update tag, given no account`()
    {
      // when
      val expected = tagRepository.save(createTagEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.updateTag(expected)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update tag, when name already exists`()
    {
      // given
      val existing = tagService.addTag(createTagModel())

      // when
      val expected = tagService.addTag(createTagModel().apply { name = "new" })
      expected.name = existing.name

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        tagService.updateTag(expected)
      }
    }
  }

  @Nested
  @DisplayName("Get Tags")
  inner class GetTagsTests
  {
    @Test
    fun `gets tags`()
    {
      // given
      tagRepository.save(createTagEntity().apply { name = "one" })
      tagRepository.save(createTagEntity().apply { name = "two" })

      // when
      val tags = tagService.getTags()

      // then
      tags.shouldNotBeEmpty()
      tags.shouldHaveSize(2)
      Assertions.assertTrue(tags.any { it.name == "one" })
      Assertions.assertTrue(tags.any { it.name == "two" })
    }
  }

  @Nested
  @DisplayName("Get Tag")
  inner class GetTagTests
  {
    @Test
    fun `gets tag`()
    {
      // when
      val expected = tagRepository.save(createTagEntity()).toModel()

      // then
      val actual = tagService.getTag(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get tag, when tag not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        tagService.getTag(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Tag")
  inner class DeleteTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes tag, given admin account`()
    {
      // when
      val tag = tagService.addTag(createTagModel())
      val tagsBefore = tagService.getTags()

      // then
      Assertions.assertDoesNotThrow {
        tagService.deleteTag(tag.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        tagService.getTag(tag.id)
      }

      val tagsAfter = tagService.getTags()

      tagsBefore.shouldContain(tag)
      tagsAfter.shouldNotContain(tag)
      tagsBefore.shouldContainAll(tagsAfter)
      tagsAfter.shouldNotContainAll(tagsBefore)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete tag, given wrong account`()
    {
      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.deleteTag(1)
      }
    }

    @Test
    fun `cannot delete tag, given no account`()
    {
      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        tagService.deleteTag(1)
      }
    }
  }
}