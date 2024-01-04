package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.exception.AlreadyExistsException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.model.Tag
import ch.barrierelos.backend.service.TagService
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@Nested
abstract class TagControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var tagService: TagService

  private val tag = Tag(
    id = 0,
    name = "name",
  )

  @Nested
  @DisplayName("Add Tag")
  inner class AddTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { tagService.addTag(tag) } returns tag

      // then
      mockMvc.post("/tag").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives tag, when provided in body`()
    {
      // when
      every { tagService.addTag(tag) } returns tag

      // then
      mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.name") { value(tag.name) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns tag, when added, given admin account`()
    {
      // when
      val expected = tag.copy()

      every { tagService.addTag(expected) } returns expected

      // then
      val actual = mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Tag>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { tagService.addTag(tag) } returns tag

      // then
      mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given wrong account`()
    {
      // when
      every { tagService.addTag(tag) } returns tag

      // then
      mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { tagService.addTag(tag) } throws NoAuthorizationException()

      // then
      mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { tagService.addTag(tag) } throws AlreadyExistsException("")

      // then
      mockMvc.post("/tag") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no tag provided in body`()
    {
      // when
      every { tagService.addTag(tag) } returns tag

      // then
      mockMvc.post("/tag").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Tag")
  inner class UpdateTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { tagService.updateTag(tag) } returns tag

      // then
      mockMvc.put("/tag/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives tag, when provided in body`()
    {
      // when
      every { tagService.updateTag(tag.apply { id = 1 }) } returns tag

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.name") { value(tag.name) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { tagService.updateTag(tag) } returns tag

      // then
      mockMvc.put("/tag/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns tag, when updated, given admin account`()
    {
      // when
      val expected = tag.copy()

      every { tagService.updateTag(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Tag>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403, given wrong account`()
    {
      // when
      every { tagService.updateTag(tag) } returns tag

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { tagService.updateTag(tag) } returns tag

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { tagService.updateTag(tag.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { tagService.updateTag(tag.apply { id = 1 }) } throws AlreadyExistsException("")

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = tag.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no tag provided in body`()
    {
      // when
      every { tagService.updateTag(tag) } returns tag

      // then
      mockMvc.put("/tag/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Tags")
  inner class GetTagsTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { tagService.getTags() } returns mutableSetOf(tag)

      // then
      mockMvc.get("/tag").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `gets tags`()
    {
      // when
      val expected = mutableSetOf(tag.copy())

      every { tagService.getTags() } returns expected

      // then
      val actual = mockMvc.get("/tag").andExpect {
        status { isOk() }
      }.body<Set<Tag>>()

      Assertions.assertEquals(expected, actual)
    }
  }

  @Nested
  @DisplayName("Get Tag")
  inner class GetTagTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { tagService.getTag(1) } returns tag

      // then
      mockMvc.get("/tag/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { tagService.getTag(1) } returns tag

      // then
      mockMvc.get("/tag/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets tag`()
    {
      // when
      val expected = tag.copy()

      every { tagService.getTag(1) } returns expected

      // then
      val actual = mockMvc.get("/tag/1").andExpect {
        status { isOk() }
      }.body<Tag>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { tagService.getTag(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/tag/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Tag")
  inner class DeleteTagTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { tagService.deleteTag(1) } returns Unit

      // then
      mockMvc.delete("/tag/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { tagService.deleteTag(1) } returns Unit

      // then
      mockMvc.delete("/tag/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { tagService.deleteTag(1) } returns Unit

      // then
      mockMvc.delete("/tag/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { tagService.deleteTag(1) } returns Unit

      // then
      mockMvc.delete("/tag/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given wrong account`()
    {
      // when
      every { tagService.deleteTag(1) } returns Unit

      // then
      mockMvc.delete("/tag/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { tagService.deleteTag(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/tag/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
