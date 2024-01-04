package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteScanModel
import ch.barrierelos.backend.message.WebsiteScanMessage
import ch.barrierelos.backend.model.WebsiteScan
import ch.barrierelos.backend.service.StatisticService
import ch.barrierelos.backend.service.WebsiteScanService
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
abstract class WebsiteScanControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var websiteScanService: WebsiteScanService

  @MockkBean
  lateinit var statisticService: StatisticService

  private val websiteScan = createWebsiteScanModel()

  @Nested
  @DisplayName("Add Website Scan")
  inner class AddWebsiteScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteScanService.addWebsiteScan(websiteScan) } returns websiteScan

      // then
      mockMvc.post("/website-scan").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website scan, when provided in body`()
    {
      // when
      every { websiteScanService.addWebsiteScan(websiteScan) } returns websiteScan

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.websiteStatistic.score") { value(websiteScan.websiteStatistic!!.score) }
          jsonPath("$.modified") { value(websiteScan.modified) }
          jsonPath("$.created") { value(websiteScan.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website scan, when added, given admin account`()
    {
      // when
      val expected = websiteScan.copy()

      every { websiteScanService.addWebsiteScan(expected) } returns expected

      // then
      val actual = mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<WebsiteScan>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } throws NoAuthorizationException()

      // then
      mockMvc.post("/website-scan") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website scan provided in body`()
    {
      // when
      every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.post("/website-scan").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update Website Scan")
  inner class UpdateWebsiteScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(websiteScan) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives website scan, when provided in body`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(websiteScan.apply { id = 1 }) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.websiteStatistic.score") { value(websiteScan.websiteStatistic!!.score) }
          jsonPath("$.modified") { value(websiteScan.modified) }
          jsonPath("$.created") { value(websiteScan.created) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(websiteScan) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns website scan, when updated, given admin account`()
    {
      // when
      val expected = websiteScan.copy()

      every { websiteScanService.updateWebsiteScan(expected.apply { id = 1 }) } returns expected

      // then
      val actual = mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<WebsiteScan>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } throws NoAuthorizationException()

      // then
      mockMvc.put("/website-scan/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = websiteScan.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no website scan provided in body`()
    {
      // when
      every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

      // then
      mockMvc.put("/website-scan/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Website Scans")
  inner class GetWebsiteScansTests
  {
    private val result = Result(
      page = 0,
      size = 1,
      totalElements = 1,
      totalPages = 1,
      lastModified = 5000,
      content = listOf(
        websiteScan
      ),
    )

    @Test
    fun `uses correct media type`()
    {
      // when
      every { websiteScanService.getWebsiteScans() } returns result

      // then
      mockMvc.get("/website-scan").andExpect {
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

      every { websiteScanService.getWebsiteScans(defaultParameters) } returns result

      // then
      mockMvc.get("/website-scan") {
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
    fun `gets website scans`()
    {
      // when
      every { websiteScanService.getWebsiteScans() } returns result

      // then
      val actual = mockMvc.get("/website-scan").andExpect {
        status { isOk() }
      }.body<List<WebsiteScan>>()

      Assertions.assertEquals(result.content, actual)
    }
  }

  @Nested
  @DisplayName("Get Website Scan")
  inner class GetWebsiteScanTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      val expected = WebsiteScanMessage(
        website = websiteScan.website,
        webpageScans = mutableSetOf(),
      )

      every { statisticService.getWebsiteScan(1) } returns expected

      // then
      mockMvc.get("/website-scan/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives path variable, when path variable provided`()
    {
      // when
      val expected = WebsiteScanMessage(
        website = websiteScan.website,
        webpageScans = mutableSetOf(),
      )

      every { statisticService.getWebsiteScan(1) } returns expected

      // then
      mockMvc.get("/website-scan/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    fun `gets website scan`()
    {
      // when
      val expected = WebsiteScanMessage(
        website = websiteScan.website,
        webpageScans = mutableSetOf(),
      )

      every { statisticService.getWebsiteScan(1) } returns expected

      // then
      val actual = mockMvc.get("/website-scan/1").andExpect {
        status { isOk() }
      }.body<WebsiteScanMessage>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { statisticService.getWebsiteScan(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/website-scan/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete Website Scan")
  inner class DeleteWebsiteScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given admin account`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given moderator account`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given contributor account`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 403 forbidden, given viewer account`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isForbidden() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } returns Unit

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { websiteScanService.deleteWebsiteScan(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/website-scan/1").andExpect {
        status { isUnauthorized() }
      }
    }
  }
}
