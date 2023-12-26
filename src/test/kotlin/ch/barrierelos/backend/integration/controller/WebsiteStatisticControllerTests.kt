package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteStatisticModel
import ch.barrierelos.backend.model.WebsiteStatistic
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteStatisticService
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
abstract class WebsiteStatisticControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var websiteStatisticService: WebsiteStatisticService

  private val websiteStatistic = createWebsiteStatisticModel()

  @Nested
  @DisplayName("Add Website Statistic")
  inner class AddWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website statistic, when provided in body`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.score") { value(websiteStatistic.score) }
          jsonPath("$.modified") { value(websiteStatistic.modified) }
          jsonPath("$.created") { value(websiteStatistic.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website statistic, when added, given admin account`()
    {
      // when
      val expected = websiteStatistic.copy()

      every { websiteStatisticService.addWebsiteStatistic(expected) } returns expected

      // then
      val actual = mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebsiteStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } throws NoAuthorizationException()

      // then
      mockMvc.post("/website-statistic") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website statistic provided in body`()
    {
      // when
      every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.post("/website-statistic").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Website Statistic")
  inner class UpdateWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website statistic, when provided in body`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic.apply { id = 1 }) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.score") { value(websiteStatistic.score) }
          jsonPath("$.modified") { value(websiteStatistic.modified) }
          jsonPath("$.created") { value(websiteStatistic.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website statistic, when updated, given admin account`()
    {
      // when
      val expected = websiteStatistic.copy()

      every { websiteStatisticService.updateWebsiteStatistic(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<WebsiteStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/website-statistic/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteStatistic.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website statistic provided in body`()
    {
      // when
      every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

      // then
      mockMvc.put("/website-statistic/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Website Statistics")
  inner class GetWebsiteStatisticsTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        websiteStatistic
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { websiteStatisticService.getWebsiteStatistics() } returns result

      // then
      mockMvc.get("/website-statistic").andExpect {
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

      every { websiteStatisticService.getWebsiteStatistics(defaultParameters) } returns result

      // then
      mockMvc.get("/website-statistic") {
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
    fun `gets website statistics`()
    {
      // when
      every { websiteStatisticService.getWebsiteStatistics() } returns result

      // then
      val actual = mockMvc.get("/website-statistic").andExpect {
        status { isOk() }
      }.body<List<WebsiteStatistic>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Website Statistic")
  inner class GetWebsiteStatisticTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { websiteStatisticService.getWebsiteStatistic(1) } returns websiteStatistic

      // then
      mockMvc.get("/website-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteStatisticService.getWebsiteStatistic(1) } returns websiteStatistic

      // then
      mockMvc.get("/website-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets website statistic`()
    {
      // when
      val expected = websiteStatistic.copy()

      every { websiteStatisticService.getWebsiteStatistic(1) } returns expected

      // then
      val actual = mockMvc.get("/website-statistic/1").andExpect {
        status { isOk() }
      }.body<WebsiteStatistic>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { websiteStatisticService.getWebsiteStatistic(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/website-statistic/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Website Statistic")
  inner class DeleteWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteStatisticService.deleteWebsiteStatistic(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/website-statistic/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
