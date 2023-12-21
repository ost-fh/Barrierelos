package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageScanModel
import ch.barrierelos.backend.model.WebpageScan
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageScanService
import ch.barrierelos.backend.util.Result
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

@Nested
abstract class WebpageScanControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var webpageScanService: WebpageScanService

  private val webpageScan = createWebpageScanModel()

  @Nested
  @DisplayName("Add Webpage Scan")
  inner class AddWebpageScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageScanService.addWebpageScan(webpageScan) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage scan, when provided in body`()
    {
      // when
      every { webpageScanService.addWebpageScan(webpageScan) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.webpageId") { value(webpageScan.webpageId) }
          jsonPath("$.userId") { value(webpageScan.userId) }
          jsonPath("$.webpageStatisticId") { value(webpageScan.webpageStatisticId) }
          jsonPath("$.webpageResultId") { value(webpageScan.webpageResultId) }
          jsonPath("$.modified") { value(webpageScan.modified) }
          jsonPath("$.created") { value(webpageScan.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage scan, when added, given admin account`()
    {
      // when
      val expected = webpageScan.copy()

      every { webpageScanService.addWebpageScan(expected) } returns expected

      // then
      val actual = mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebpageScan>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/webpage-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage scan provided in body`()
    {
      // when
      every { webpageScanService.addWebpageScan(any()) } returns webpageScan

      // then
      mockMvc.post("/webpage-scan").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Scans")
  inner class GetWebpageScansTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      count = 1,
      lastModified = 5000,
      content = listOf(
        webpageScan
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageScanService.getWebpageScans() } returns result

      // then
      mockMvc.get("/webpage-scan").andExpect {
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
        sort = "id",
        order = OrderEnum.ASC,
        modifiedAfter = 4000
      )

      every { webpageScanService.getWebpageScans(defaultParameters) } returns result

      // then
      mockMvc.get("/webpage-scan") {
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
    fun `gets webpage scans`()
    {
      // when
      every { webpageScanService.getWebpageScans() } returns result

      // then
      val actual = mockMvc.get("/webpage-scan").andExpect {
        status { isOk() }
      }.body<List<WebpageScan>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Webpage Scan")
  inner class GetWebpageScanTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageScanService.getWebpageScan(1) } returns webpageScan

      // then
      mockMvc.get("/webpage-scan/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageScanService.getWebpageScan(1) } returns webpageScan

      // then
      mockMvc.get("/webpage-scan/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets webpage scan`()
    {
      // when
      val expected = webpageScan.copy()

      every { webpageScanService.getWebpageScan(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage-scan/1").andExpect {
        status { isOk() }
      }.body<WebpageScan>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { webpageScanService.getWebpageScan(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/webpage-scan/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Scan")
  inner class DeleteWebpageScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } returns Unit

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageScanService.deleteWebpageScan(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/webpage-scan/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
