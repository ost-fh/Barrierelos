package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageStatisticModel
import ch.barrierelos.backend.model.WebpageStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageStatisticService
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
abstract class WebpageStatisticControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var webpageStatisticService: WebpageStatisticService

  private val webpageStatistic = createWebpageStatisticModel()

  @Nested
  @DisplayName("Add Webpage Statistic")
  inner class AddWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage statistic, when provided in body`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.score") { value(webpageStatistic.score) }
          jsonPath("$.modified") { value(webpageStatistic.modified) }
          jsonPath("$.created") { value(webpageStatistic.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage statistic, when added, given admin account`()
    {
      // when
      val expected = webpageStatistic.copy()

      every { webpageStatisticService.addWebpageStatistic(expected) } returns expected

      // then
      val actual = mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebpageStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } throws NoAuthorizationException()

      // then
      mockMvc.post("/webpage-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage statistic provided in body`()
    {
      // when
      every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.post("/webpage-statistic").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Webpage Statistic")
  inner class UpdateWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives webpage statistic, when provided in body`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic.apply { id = 1 }) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.score") { value(webpageStatistic.score) }
          jsonPath("$.modified") { value(webpageStatistic.modified) }
          jsonPath("$.created") { value(webpageStatistic.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns webpage statistic, when updated, given admin account`()
    {
      // when
      val expected = webpageStatistic.copy()

      every { webpageStatisticService.updateWebpageStatistic(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<WebpageStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/webpage-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = webpageStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no webpage statistic provided in body`()
    {
      // when
      every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

      // then
      mockMvc.put("/webpage-statistic/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Statistics")
  inner class GetWebpageStatisticsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        webpageStatistic
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageStatisticService.getWebpageStatistics() } returns result

      // then
      mockMvc.get("/webpage-statistic").andExpect {
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
        sort = "score",
        order = OrderEnum.ASC,
        modifiedAfter = 4000
      )

      every { webpageStatisticService.getWebpageStatistics(defaultParameters) } returns result

      // then
      mockMvc.get("/webpage-statistic") {
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
    fun `gets webpage statistics`()
    {
      // when
      every { webpageStatisticService.getWebpageStatistics() } returns result

      // then
      val actual = mockMvc.get("/webpage-statistic").andExpect {
        status { isOk() }
      }.body<List<WebpageStatistic>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Webpage Statistic")
  inner class GetWebpageStatisticTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { webpageStatisticService.getWebpageStatistic(1) } returns webpageStatistic

      // then
      mockMvc.get("/webpage-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageStatisticService.getWebpageStatistic(1) } returns webpageStatistic

      // then
      mockMvc.get("/webpage-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets webpage statistic`()
    {
      // when
      val expected = webpageStatistic.copy()

      every { webpageStatisticService.getWebpageStatistic(1) } returns expected

      // then
      val actual = mockMvc.get("/webpage-statistic/1").andExpect {
        status { isOk() }
      }.body<WebpageStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { webpageStatisticService.getWebpageStatistic(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/webpage-statistic/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Statistic")
  inner class DeleteWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { webpageStatisticService.deleteWebpageStatistic(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/webpage-statistic/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
