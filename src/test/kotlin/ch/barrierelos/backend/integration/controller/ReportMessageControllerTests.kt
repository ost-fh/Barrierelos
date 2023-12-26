package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createReportMessageModel
import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.ReportMessageService
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
abstract class ReportMessageControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var reportMessageService: ReportMessageService

  private val reportMessage = createReportMessageModel()

  @Nested
  @DisplayName("Add Report Message")
  inner class AddReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportMessageService.addReportMessage(any()) } returns reportMessage

      // then
      mockMvc.post("/report-message").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives report message, when provided in body`()
    {
      // when
      every { reportMessageService.addReportMessage(reportMessage) } returns reportMessage

      // then
      mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.reportId") { value(reportMessage.reportId) }
          jsonPath("$.userId") { value(reportMessage.userId) }
          jsonPath("$.message") { value(reportMessage.message) }
          jsonPath("$.modified") { value(reportMessage.modified) }
          jsonPath("$.created") { value(reportMessage.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report message, when added, given admin account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.addReportMessage(expected) } returns expected

      // then
      val actual = mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report message, when added, given moderator account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.addReportMessage(expected) } returns expected

      // then
      val actual = mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report message, when added, given contributor account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.addReportMessage(expected) } returns expected

      // then
      val actual = mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportMessageService.addReportMessage(any()) } returns reportMessage

      // then
      mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportMessageService.addReportMessage(any()) } returns reportMessage

      // then
      mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportMessageService.addReportMessage(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/report-message") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no report message provided in body`()
    {
      // when
      every { reportMessageService.addReportMessage(any()) } returns reportMessage

      // then
      mockMvc.post("/report-message").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Report Message")
  inner class UpdateReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives report message, when provided in body`()
    {
      // when
      every { reportMessageService.updateReportMessage(reportMessage.apply { id = 1 }) } returns reportMessage

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.reportId") { value(reportMessage.reportId) }
          jsonPath("$.userId") { value(reportMessage.userId) }
          jsonPath("$.message") { value(reportMessage.message) }
          jsonPath("$.modified") { value(reportMessage.modified) }
          jsonPath("$.created") { value(reportMessage.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns report message, when updated, given admin account`()
    {
      // when
      val expected = reportMessage.copy().apply { id = 1 }

      every { reportMessageService.updateReportMessage(expected) } returns expected

      // then
      val actual = mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } throws NoAuthorizationException()

      // then
      mockMvc.put("/report-message/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = reportMessage.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no report message provided in body`()
    {
      // when
      every { reportMessageService.updateReportMessage(any()) } returns reportMessage

      // then
      mockMvc.put("/report-message/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Report Messages")
  inner class GetReportMessagesTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        reportMessage
      ),
    )

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      mockMvc.get("/report-message").andExpect {
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

      every { reportMessageService.getReportMessages(defaultParameters) } returns result

      // then
      mockMvc.get("/report-message") {
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
    fun `gets report messages, given admin account`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      val actual = mockMvc.get("/report-message").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages, given moderator account`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      val actual = mockMvc.get("/report-message").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages, given contributor account`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      val actual = mockMvc.get("/report-message").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      mockMvc.get("/report-message").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportMessageService.getReportMessages() } returns result

      // then
      mockMvc.get("/report-message").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given admin account`()
    {
      // when
      every { reportMessageService.getReportMessagesByReport(1) } returns result

      // then
      val actual = mockMvc.get("/report-message?reportId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given moderator account`()
    {
      // when
      every { reportMessageService.getReportMessagesByReport(1) } returns result

      // then
      val actual = mockMvc.get("/report-message?reportId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given contributor account`()
    {
      // when
      every { reportMessageService.getReportMessagesByReport(1, any()) } returns result

      // then
      val actual = mockMvc.get("/report-message?reportId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given admin account`()
    {
      // when
      every { reportMessageService.getReportMessagesByUser(1) } returns result

      // then
      val actual = mockMvc.get("/report-message?userId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given moderator account`()
    {
      // when
      every { reportMessageService.getReportMessagesByUser(1) } returns result

      // then
      val actual = mockMvc.get("/report-message?userId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given contributor account`()
    {
      // when
      every { reportMessageService.getReportMessagesByUser(1, any()) } returns result

      // then
      val actual = mockMvc.get("/report-message?userId=1").andExpect {
        status { isOk() }
      }.body<List<ReportMessage>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Report Message")
  inner class GetReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportMessageService.getReportMessage(1) } returns reportMessage

      // then
      mockMvc.get("/report-message/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportMessageService.getReportMessage(1) } returns reportMessage

      // then
      mockMvc.get("/report-message/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given admin account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.getReportMessage(1) } returns expected

      // then
      val actual = mockMvc.get("/report-message/1").andExpect {
        status { isOk() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given moderator account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.getReportMessage(1) } returns expected

      // then
      val actual = mockMvc.get("/report-message/1").andExpect {
        status { isOk() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given contributor account`()
    {
      // when
      val expected = reportMessage.copy()

      every { reportMessageService.getReportMessage(1) } returns expected

      // then
      val actual = mockMvc.get("/report-message/1").andExpect {
        status { isOk() }
      }.body<ReportMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportMessageService.getReportMessage(1) } returns reportMessage

      // then
      mockMvc.get("/report-message/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportMessageService.getReportMessage(1) } returns reportMessage

      // then
      mockMvc.get("/report-message/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { reportMessageService.getReportMessage(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/report-message/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Report Message")
  inner class DeleteReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } returns Unit

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { reportMessageService.deleteReportMessage(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/report-message/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
