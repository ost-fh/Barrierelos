package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageReportModel
import ch.barrierelos.backend.model.WebpageReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageReportService
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
abstract class WebpageReportControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var webpageReportService: WebpageReportService

  private val webpageReport = createWebpageReportModel()

  @Nested
  @DisplayName("Add Webpage Report")
  inner class AddWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageReportService.addWebpageReport(any()) } returns webpageReport

      // then
      mockMvc.post("/webpage-report").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage report, when provided in body`()
    {
      // when
      every {
        webpageReportService.addWebpageReport(webpageReport.apply {
          report.reason = ReasonEnum.INCORRECT
          report.state = StateEnum.CLOSED
        })
      } returns webpageReport

      // then
      mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageReport.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.webpageId") { value(webpageReport.webpageId) }
          jsonPath("$.report.userId") { value(webpageReport.report.userId) }
          jsonPath("$.report.reason") { value(webpageReport.report.reason.toString()) }
          jsonPath("$.report.state") { value(webpageReport.report.state.toString()) }
          jsonPath("$.report.modified") { value(webpageReport.report.modified) }
          jsonPath("$.report.created") { value(webpageReport.report.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage report, when added, given admin account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.addWebpageReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage report, when added, given moderator account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.addWebpageReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage report, when added, given contributor account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.addWebpageReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageReportService.addWebpageReport(any()) } returns webpageReport

      // then
      mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageReport.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageReportService.addWebpageReport(any()) } returns webpageReport

      // then
      mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageReportService.addWebpageReport(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/webpage-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage report provided in body`()
    {
      // when
      every { webpageReportService.addWebpageReport(any()) } returns webpageReport

      // then
      mockMvc.post("/webpage-report").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Reports")
  inner class GetWebpageReportsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      count = 1,
      lastModified = 5000,
      content = listOf(
        webpageReport
      ),
    )

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      mockMvc.get("/webpage-report").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
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

      every { webpageReportService.getWebpageReports(defaultParameters) } returns result

      // then
      mockMvc.get("/webpage-report") {
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
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given admin account`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      val actual = mockMvc.get("/webpage-report").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given moderator account`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      val actual = mockMvc.get("/webpage-report").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given contributor account`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      val actual = mockMvc.get("/webpage-report").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      mockMvc.get("/webpage-report").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageReportService.getWebpageReports() } returns result

      // then
      mockMvc.get("/webpage-report").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given admin account`()
    {
      // when
      every { webpageReportService.getWebpageReportsByWebpage(1) } returns result

      // then
      val actual = mockMvc.get("/webpage-report?webpageId=1").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given moderator account`()
    {
      // when
      every { webpageReportService.getWebpageReportsByWebpage(1) } returns result

      // then
      val actual = mockMvc.get("/webpage-report?webpageId=1").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given contributor account`()
    {
      // when
      every { webpageReportService.getWebpageReportsByWebpage(1, any()) } returns result

      // then
      val actual = mockMvc.get("/webpage-report?webpageId=1").andExpect {
        status { isOk() }
      }.body<List<WebpageReport>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @DisplayName("Get Webpage Report")
  @Nested
  inner class GetWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageReportService.getWebpageReport(1) } returns webpageReport

      // then
      mockMvc.get("/webpage-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageReportService.getWebpageReport(1) } returns webpageReport

      // then
      mockMvc.get("/webpage-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given admin account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.getWebpageReport(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage-report/1").andExpect {
        status { isOk() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given moderator account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.getWebpageReport(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage-report/1").andExpect {
        status { isOk() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given contributor account`()
    {
      // when
      val expected = webpageReport.copy()

      every { webpageReportService.getWebpageReport(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage-report/1").andExpect {
        status { isOk() }
      }.body<WebpageReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageReportService.getWebpageReport(1) } returns webpageReport

      // then
      mockMvc.get("/webpage-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageReportService.getWebpageReport(1) } returns webpageReport

      // then
      mockMvc.get("/webpage-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { webpageReportService.getWebpageReport(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/webpage-report/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Report")
  inner class DeleteWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } returns Unit

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageReportService.deleteWebpageReport(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/webpage-report/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
