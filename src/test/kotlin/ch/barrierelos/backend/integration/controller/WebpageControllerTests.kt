package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exception.AlreadyExistsException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageMessage
import ch.barrierelos.backend.helper.createWebpageModel
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageService
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
abstract class WebpageControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var webpageService: WebpageService

  private val webpageMessage = createWebpageMessage()
  private val webpage = createWebpageModel()

  @Nested
  inner class AddWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      mockMvc.post("/webpage").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage, when provided in body`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.user.username") { value(webpage.user.username) }
          jsonPath("$.displayUrl") { value(webpage.displayUrl) }
          jsonPath("$.url") { value(webpage.url) }
          jsonPath("$.modified") { value(webpage.modified) }
          jsonPath("$.created") { value(webpage.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage, when added, given admin account`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      val actual = mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Webpage>()

      Assertions.assertEquals(webpage, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage, when added, given moderator account`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      val actual = mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Webpage>()

      Assertions.assertEquals(webpage, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage, when added, given contributor account`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      val actual = mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Webpage>()

      Assertions.assertEquals(webpage, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } throws NoAuthorizationException()

      // then
      mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } throws AlreadyExistsException("")

      // then
      mockMvc.post("/webpage") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageMessage.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage provided in body`()
    {
      // when
      every { webpageService.addWebpage(webpageMessage) } returns webpage

      // then
      mockMvc.post("/webpage").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  inner class UpdateWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/webpage/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage, when provided in body`()
    {
      // when
      every { webpageService.updateWebpage(webpage.apply {
        id = 1
        status = StatusEnum.READY
      }) } returns webpage

      // then
      mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.user.username") { value(webpage.user.username) }
          jsonPath("$.displayUrl") { value(webpage.displayUrl) }
          jsonPath("$.url") { value(webpage.url) }
          jsonPath("$.status") { value(webpage.status.toString()) }
          jsonPath("$.modified") { value(webpage.modified) }
          jsonPath("$.created") { value(webpage.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/webpage/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage, when updated, given admin account`()
    {
      // when
      val expected = webpage.copy()

      every { webpageService.updateWebpage(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Webpage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage, when updated, given moderator account`()
    {
      // when
      val expected = webpage.copy()

      every { webpageService.updateWebpage(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Webpage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/tag/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageService.updateWebpage(webpage.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { webpageService.updateWebpage(webpage.apply { id = 1 }) } throws AlreadyExistsException("")

      // then
      mockMvc.put("/webpage/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpage.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage provided in body`()
    {
      // when
      every { webpageService.updateWebpage(webpage) } returns webpage

      // then
      mockMvc.put("/webpage/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  inner class GetWebpagesTests
  {
    val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        webpage
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageService.getWebpages() } returns result

      // then
      mockMvc.get("/webpage").andExpect {
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

      every { webpageService.getWebpages(defaultParameters = defaultParameters) } returns result

      // then
      mockMvc.get("/webpage") {
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
    fun `gets webpages`()
    {
      // when
      every { webpageService.getWebpages() } returns result

      // then
      val actual = mockMvc.get("/webpage").andExpect {
        status { isOk() }
      }.body<List<Webpage>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  inner class GetWebpageTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageService.getWebpage(1) } returns webpage

      // then
      mockMvc.get("/webpage/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageService.getWebpage(1) } returns webpage

      // then
      mockMvc.get("/webpage/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets webpage`()
    {
      // when
      val expected = webpage.copy()

      every { webpageService.getWebpage(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage/1").andExpect {
        status { isOk() }
      }.body<Webpage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { webpageService.getWebpage(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/webpage/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  inner class DeleteWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given moderator account`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageService.deleteWebpage(1) } returns Unit

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageService.deleteWebpage(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/webpage/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
