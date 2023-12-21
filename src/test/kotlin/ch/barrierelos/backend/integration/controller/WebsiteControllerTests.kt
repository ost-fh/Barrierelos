package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteMessage
import ch.barrierelos.backend.helper.createWebsiteModel
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteService
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@Nested
abstract class WebsiteControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var websiteService: WebsiteService

  private val websiteMessage = createWebsiteMessage()
  private val website = createWebsiteModel()

  @Nested
  inner class AddWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      mockMvc.post("/website").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website, when provided in body`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.user.username") { value(website.user.username) }
          jsonPath("$.domain") { value(website.domain) }
          jsonPath("$.url") { value(website.url) }
          jsonPath("$.category") { value(website.category.toString()) }
          jsonPath("$.tags[0].tag.name") { value(website.tags.first().tag.name) }
          jsonPath("$.modified") { value(website.modified) }
          jsonPath("$.created") { value(website.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when added, given admin account`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      val actual = mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Website>()

      Assertions.assertEquals(website, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when added, given moderator account`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      val actual = mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Website>()

      Assertions.assertEquals(website, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when added, given contributor account`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      val actual = mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Website>()

      Assertions.assertEquals(website, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } throws NoAuthorizationException()

      // then
      mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } throws AlreadyExistsException("")

      // then
      mockMvc.post("/website") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteMessage.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website provided in body`()
    {
      // when
      every { websiteService.addWebsite(websiteMessage) } returns website

      // then
      mockMvc.post("/website").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  inner class UpdateWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteService.updateWebsite(website) } returns website

      // then
      mockMvc.put("/website/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website, when provided in body`()
    {
      // when
      every { websiteService.updateWebsite(website.apply {
        id = 1
        status = StatusEnum.READY
      }) } returns website

      // then
      mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = website.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.user.username") { value(website.user.username) }
          jsonPath("$.domain") { value(website.domain) }
          jsonPath("$.url") { value(website.url) }
          jsonPath("$.category") { value(website.category.toString()) }
          jsonPath("$.status") { value(website.status.toString()) }
          jsonPath("$.tags[0].tag.name") { value(website.tags.first().tag.name) }
          jsonPath("$.modified") { value(website.modified) }
          jsonPath("$.created") { value(website.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteService.updateWebsite(website) } returns website

      // then
      mockMvc.put("/website/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when updated, given admin account`()
    {
      // when
      val expected = website.copy()

      every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Website>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when updated, given moderator account`()
    {
      // when
      val expected = website.copy()

      every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Website>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website, when updated, given contributor account`()
    {
      // when
      val expected = website.copy()

      every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Website>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteService.updateWebsite(website) } returns website

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = website.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteService.updateWebsite(website) } returns website

      // then
      mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = website.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteService.updateWebsite(website.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = website.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { websiteService.updateWebsite(website.apply { id = 1 }) } throws AlreadyExistsException("")

      // then
      mockMvc.put("/website/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = website.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website provided in body`()
    {
      // when
      every { websiteService.updateWebsite(website) } returns website

      // then
      mockMvc.put("/website/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  inner class GetWebsitesTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      count = 1,
      lastModified = 5000,
      content = listOf(
        website
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { websiteService.getWebsites() } returns result

      // then
      mockMvc.get("/website").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives default parameters, when default parameters provided`()
    {
      // when
      val defaultParameters = DefaultParameters(
        page = 0,
        size = 2,
        sort = "domain",
        order = OrderEnum.ASC,
        modifiedAfter = 4000
      )

      every { websiteService.getWebsites(defaultParameters = defaultParameters) } returns result

      // then
      mockMvc.get("/website") {
        param("page", defaultParameters.page.toString())
        param("size", defaultParameters.size.toString())
        param("sort", defaultParameters.sort.toString())
        param("order", defaultParameters.order.toString())
        param("modifiedAfter", defaultParameters.modifiedAfter.toString())
      }.andExpect {
        haveParameterValue("page", defaultParameters.page.toString())
        haveParameterValue("size", defaultParameters.size.toString())
        haveParameterValue("sort", defaultParameters.sort.toString())
        haveParameterValue("order", defaultParameters.order.toString())
        haveParameterValue("modifiedAfter", defaultParameters.modifiedAfter.toString())
      }
    }

    @Test
    fun `gets websites`()
    {
      // when
      every { websiteService.getWebsites() } returns result

      // then
      val actual = mockMvc.get("/website").andExpect {
        status { isOk() }
      }.body<List<Website>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  inner class GetWebsiteTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { websiteService.getWebsite(1) } returns website

      // then
      mockMvc.get("/website/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteService.getWebsite(1) } returns website

      // then
      mockMvc.get("/website/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets website`()
    {
      // when
      val expected = website.copy()

      every { websiteService.getWebsite(1) } returns expected

      // then
      val actual = mockMvc.get("/website/1").andExpect {
        status { isOk() }
      }.body<Website>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { websiteService.getWebsite(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/website/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  inner class DeleteWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given moderator account`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteService.deleteWebsite(1) } returns Unit

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteService.deleteWebsite(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/website/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
