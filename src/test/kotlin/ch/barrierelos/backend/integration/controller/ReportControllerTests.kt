package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createReportModel
import ch.barrierelos.backend.model.Report
import ch.barrierelos.backend.service.ReportService
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
import org.springframework.test.web.servlet.put

@Nested
abstract class ReportControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var reportService: ReportService

  private val report = createReportModel()

  @Nested
  @DisplayName("Add Report")
  inner class AddReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportService.addReport(any()) } returns report

      // then
      mockMvc.post("/report").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives report, when provided in body`()
    {
      // when
      every {
        reportService.addReport(report.apply {
          reason = ReasonEnum.INCORRECT
          state = StateEnum.CLOSED
        })
      } returns report

      // then
      mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.userId") { value(report.userId) }
          jsonPath("$.reason") { value(report.reason.toString()) }
          jsonPath("$.state") { value(report.state.toString()) }
          jsonPath("$.modified") { value(report.modified) }
          jsonPath("$.created") { value(report.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report, when added, given admin account`()
    {
      // when
      val expected = report.copy()

      every { reportService.addReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report, when added, given moderator account`()
    {
      // when
      val expected = report.copy()

      every { reportService.addReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report, when added, given contributor account`()
    {
      // when
      val expected = report.copy()

      every { reportService.addReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportService.addReport(any()) } returns report

      // then
      mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportService.addReport(any()) } returns report

      // then
      mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportService.addReport(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no report provided in body`()
    {
      // when
      every { reportService.addReport(any()) } returns report

      // then
      mockMvc.post("/report").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Report")
  inner class UpdateReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives report, when provided in body`()
    {
      // when
      every {
        reportService.updateReport(report.apply {
          id = 1
          reason = ReasonEnum.INCORRECT
          state = StateEnum.CLOSED
        })
      } returns report

      // then
      mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.userId") { value(report.userId) }
          jsonPath("$.reason") { value(report.reason.toString()) }
          jsonPath("$.state") { value(report.state.toString()) }
          jsonPath("$.modified") { value(report.modified) }
          jsonPath("$.created") { value(report.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report, when updated, given admin account`()
    {
      // when
      val expected = report.copy().apply { id = 1 }

      every { reportService.updateReport(expected) } returns expected

      // then
      val actual = mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report, when updated, given moderator account`()
    {
      // when
      val expected = report.copy().apply { id = 1 }

      every { reportService.updateReport(expected) } returns expected

      // then
      val actual = mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportService.updateReport(any()) } throws NoAuthorizationException()

      // then
      mockMvc.put("/report/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = report.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no report provided in body`()
    {
      // when
      every { reportService.updateReport(any()) } returns report

      // then
      mockMvc.put("/report/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Reports")
  inner class GetReportsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        report
      ),
    )

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      mockMvc.get("/report").andExpect {
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

      every { reportService.getReports(defaultParameters) } returns result

      // then
      mockMvc.get("/report") {
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
    fun `gets reports, given admin account`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      val actual = mockMvc.get("/report").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports, given moderator account`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      val actual = mockMvc.get("/report").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports, given contributor account`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      val actual = mockMvc.get("/report").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      mockMvc.get("/report").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportService.getReports() } returns result

      // then
      mockMvc.get("/report").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given admin account`()
    {
      // when
      every { reportService.getReportsByUser(1) } returns result

      // then
      val actual = mockMvc.get("/report?userId=1").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given moderator account`()
    {
      // when
      every { reportService.getReportsByUser(1) } returns result

      // then
      val actual = mockMvc.get("/report?userId=1").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given contributor account`()
    {
      // when
      every { reportService.getReportsByUser(1, any()) } returns result

      // then
      val actual = mockMvc.get("/report?userId=1").andExpect {
        status { isOk() }
      }.body<List<Report>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Report")
  inner class GetReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportService.getReport(1) } returns report

      // then
      mockMvc.get("/report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportService.getReport(1) } returns report

      // then
      mockMvc.get("/report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given admin account`()
    {
      // when
      val expected = report.copy()

      every { reportService.getReport(1) } returns expected

      // then
      val actual = mockMvc.get("/report/1").andExpect {
        status { isOk() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given moderator account`()
    {
      // when
      val expected = report.copy()

      every { reportService.getReport(1) } returns expected

      // then
      val actual = mockMvc.get("/report/1").andExpect {
        status { isOk() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given contributor account`()
    {
      // when
      val expected = report.copy()

      every { reportService.getReport(1) } returns expected

      // then
      val actual = mockMvc.get("/report/1").andExpect {
        status { isOk() }
      }.body<Report>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportService.getReport(1) } returns report

      // then
      mockMvc.get("/report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportService.getReport(1) } returns report

      // then
      mockMvc.get("/report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { reportService.getReport(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/report/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Report")
  inner class DeleteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportService.deleteReport(1) } returns Unit

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportService.deleteReport(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/report/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
