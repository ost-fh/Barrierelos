package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createUserReportModel
import ch.barrierelos.backend.model.UserReport
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.UserReportService
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
abstract class UserReportControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var userReportService: UserReportService

  private val userReport = createUserReportModel()

  @Nested
  @DisplayName("Add User Report")
  inner class AddUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userReportService.addUserReport(any()) } returns userReport

      // then
      mockMvc.post("/user-report").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives user report, when provided in body`()
    {
      // when
      every {
        userReportService.addUserReport(userReport.apply {
          report.reason = ReasonEnum.INCORRECT
          report.state = StateEnum.CLOSED
        })
      } returns userReport

      // then
      mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = userReport.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.userId") { value(userReport.userId) }
          jsonPath("$.report.userId") { value(userReport.report.userId) }
          jsonPath("$.report.reason") { value(userReport.report.reason.toString()) }
          jsonPath("$.report.state") { value(userReport.report.state.toString()) }
          jsonPath("$.report.modified") { value(userReport.report.modified) }
          jsonPath("$.report.created") { value(userReport.report.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns user report, when added, given admin account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.addUserReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns user report, when added, given moderator account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.addUserReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns user report, when added, given contributor account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.addUserReport(expected) } returns expected

      // then
      val actual = mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { userReportService.addUserReport(any()) } returns userReport

      // then
      mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = userReport.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userReportService.addUserReport(any()) } returns userReport

      // then
      mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = userReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userReportService.addUserReport(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/user-report") {
        contentType = EXPECTED_MEDIA_TYPE
        content = userReport.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no user report provided in body`()
    {
      // when
      every { userReportService.addUserReport(any()) } returns userReport

      // then
      mockMvc.post("/user-report").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get User Reports")
  inner class GetUserReportsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        userReport
      ),
    )

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      mockMvc.get("/user-report").andExpect {
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

      every { userReportService.getUserReports(defaultParameters) } returns result

      // then
      mockMvc.get("/user-report") {
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
    fun `gets user reports, given admin account`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      val actual = mockMvc.get("/user-report").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports, given moderator account`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      val actual = mockMvc.get("/user-report").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports, given contributor account`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      val actual = mockMvc.get("/user-report").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      mockMvc.get("/user-report").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userReportService.getUserReports() } returns result

      // then
      mockMvc.get("/user-report").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given admin account`()
    {
      // when
      every { userReportService.getUserReportsByUser(1) } returns result

      // then
      val actual = mockMvc.get("/user-report?userId=1").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given moderator account`()
    {
      // when
      every { userReportService.getUserReportsByUser(1) } returns result

      // then
      val actual = mockMvc.get("/user-report?userId=1").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given contributor account`()
    {
      // when
      every { userReportService.getUserReportsByUser(1, any()) } returns result

      // then
      val actual = mockMvc.get("/user-report?userId=1").andExpect {
        status { isOk() }
      }.body<List<UserReport>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get User Report")
  inner class GetUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userReportService.getUserReport(1) } returns userReport

      // then
      mockMvc.get("/user-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { userReportService.getUserReport(1) } returns userReport

      // then
      mockMvc.get("/user-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given admin account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.getUserReport(1) } returns expected

      // then
      val actual = mockMvc.get("/user-report/1").andExpect {
        status { isOk() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given moderator account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.getUserReport(1) } returns expected

      // then
      val actual = mockMvc.get("/user-report/1").andExpect {
        status { isOk() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given contributor account`()
    {
      // when
      val expected = userReport.copy()

      every { userReportService.getUserReport(1) } returns expected

      // then
      val actual = mockMvc.get("/user-report/1").andExpect {
        status { isOk() }
      }.body<UserReport>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { userReportService.getUserReport(1) } returns userReport

      // then
      mockMvc.get("/user-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userReportService.getUserReport(1) } returns userReport

      // then
      mockMvc.get("/user-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { userReportService.getUserReport(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/user-report/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete User Report")
  inner class DeleteUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userReportService.deleteUserReport(1) } returns Unit

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userReportService.deleteUserReport(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/user-report/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
