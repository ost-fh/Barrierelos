package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteReportModel
import ch.barrierelos.backend.model.WebsiteReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteReportService
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
abstract class WebsiteReportControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var websiteReportService: WebsiteReportService

  private val websiteReport = createWebsiteReportModel()

  @Nested
  @DisplayName("Add Website Report")
  inner class AddWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteReportService.addWebsiteReport(any()) } returns websiteReport

      // then
      mockMvc.post("/website-report").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website report, when provided in body`()
    {
      // when
      every {
        websiteReportService.addWebsiteReport(websiteReport.apply {
          report.reason = ReasonEnum.INCORRECT
          report.state = StateEnum.CLOSED
        })
      } returns websiteReport

      // then
      mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteReport.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.websiteId") { value(websiteReport.websiteId) }
          jsonPath("$.report.userId") { value(websiteReport.report.userId) }
          jsonPath("$.report.reason") { value(websiteReport.report.reason.toString()) }
          jsonPath("$.report.state") { value(websiteReport.report.state.toString()) }
          jsonPath("$.report.modified") { value(websiteReport.report.modified) }
          jsonPath("$.report.created") { value(websiteReport.report.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website report, when added, given admin account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.addWebsiteReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website report, when added, given moderator account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.addWebsiteReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website report, when added, given contributor account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.addWebsiteReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteReportService.addWebsiteReport(any()) } returns websiteReport

      // then
      mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteReport.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteReportService.addWebsiteReport(any()) } returns websiteReport

      // then
      mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteReportService.addWebsiteReport(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/website-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website report provided in body`()
    {
      // when
      every { websiteReportService.addWebsiteReport(any()) } returns websiteReport

      // then
      mockMvc.post("/website-report").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Website Reports")
  inner class GetWebsiteReportsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      count = 1,
      lastModified = 5000,
      content = listOf(
        websiteReport
      ),
    )

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      mockMvc.get("/website-report").andExpect {
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

      every { websiteReportService.getWebsiteReports(defaultParameters) } returns result

      // then
      mockMvc.get("/website-report") {
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
    fun `gets website reports, given admin account`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      val actual = mockMvc.get("/website-report").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports, given moderator account`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      val actual = mockMvc.get("/website-report").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports, given contributor account`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      val actual = mockMvc.get("/website-report").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      mockMvc.get("/website-report").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteReportService.getWebsiteReports() } returns result

      // then
      mockMvc.get("/website-report").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given admin account`()
    {
      // when
      every { websiteReportService.getWebsiteReportsByWebsite(1) } returns result

      // then
      val actual = mockMvc.get("/website-report?websiteId=1").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given moderator account`()
    {
      // when
      every { websiteReportService.getWebsiteReportsByWebsite(1) } returns result

      // then
      val actual = mockMvc.get("/website-report?websiteId=1").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given contributor account`()
    {
      // when
      every { websiteReportService.getWebsiteReportsByWebsite(1, any()) } returns result

      // then
      val actual = mockMvc.get("/website-report?websiteId=1").andExpect {
        status { isOk() }
      }.body<List<WebsiteReport>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Website Report")
  inner class GetWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteReportService.getWebsiteReport(1) } returns websiteReport

      // then
      mockMvc.get("/website-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteReportService.getWebsiteReport(1) } returns websiteReport

      // then
      mockMvc.get("/website-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given admin account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.getWebsiteReport(1) } returns expected

      // then
      val actual = mockMvc.get("/website-report/1").andExpect {
        status { isOk() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given moderator account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.getWebsiteReport(1) } returns expected

      // then
      val actual = mockMvc.get("/website-report/1").andExpect {
        status { isOk() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given contributor account`()
    {
      // when
      val expected = websiteReport.copy()

      every { websiteReportService.getWebsiteReport(1) } returns expected

      // then
      val actual = mockMvc.get("/website-report/1").andExpect {
        status { isOk() }
      }.body<WebsiteReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteReportService.getWebsiteReport(1) } returns websiteReport

      // then
      mockMvc.get("/website-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteReportService.getWebsiteReport(1) } returns websiteReport

      // then
      mockMvc.get("/website-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { websiteReportService.getWebsiteReport(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/website-report/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Website Report")
  inner class DeleteWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } returns Unit

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteReportService.deleteWebsiteReport(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/website-report/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
