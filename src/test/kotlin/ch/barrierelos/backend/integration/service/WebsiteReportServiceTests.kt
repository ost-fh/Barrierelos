package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteReportEntity
import ch.barrierelos.backend.helper.createWebsiteReportModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteReportService
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class WebsiteReportServiceTests : ServiceTests()
{
  @Autowired
  lateinit var websiteReportService: WebsiteReportService

  @Nested
  @DisplayName("Add WebsiteReport")
  inner class AddWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds website report, given admin account`()
    {
      // when
      val expected = createWebsiteReportModel()

      // then
      val actual = websiteReportService.addWebsiteReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.websiteId, actual.websiteId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds website report, given moderator account`()
    {
      // when
      val expected = createWebsiteReportModel()

      // then
      val actual = websiteReportService.addWebsiteReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.websiteId, actual.websiteId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds website report, given contributor account`()
    {
      // when
      val expected = createWebsiteReportModel()

      // then
      val actual = websiteReportService.addWebsiteReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.websiteId, actual.websiteId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website report, given viewer account`()
    {
      // when
      val websiteReport = createWebsiteReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.addWebsiteReport(websiteReport)
      }
    }

    @Test
    fun `cannot add website report, given no account`()
    {
      // when
      val websiteReport = createWebsiteReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.addWebsiteReport(websiteReport)
      }
    }
  }

  @Nested
  @DisplayName("Get WebsiteReports")
  inner class GetWebsiteReportsTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports, given admin account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 60L))

      // when
      val websiteReports = websiteReportService.getWebsiteReports().content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
      Assertions.assertTrue(websiteReports.any { it.websiteId == 60L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports, given moderator account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 60L))

      // when
      val websiteReports = websiteReportService.getWebsiteReports().content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
      Assertions.assertTrue(websiteReports.any { it.websiteId == 60L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports, given contributor account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 60L))

      // when
      val websiteReports = websiteReportService.getWebsiteReports().content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
      Assertions.assertTrue(websiteReports.any { it.websiteId == 60L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get website reports, given viewer account`()
    {
      // when
      websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.getWebsiteReports()
      }
    }

    @Test
    fun `cannot get website reports, given no account`()
    {
      // when
      websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.getWebsiteReports()
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given admin account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))

      // when
      val websiteReports = websiteReportService.getWebsiteReportsByWebsite(40).content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given moderator account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))

      // when
      val websiteReports = websiteReportService.getWebsiteReportsByWebsite(40).content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports by websiteId, given contributor account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))

      // when
      val websiteReports = websiteReportService.getWebsiteReportsByWebsite(40).content

      // then
      websiteReports.shouldNotBeEmpty()
      websiteReports.shouldHaveSize(2)
      Assertions.assertTrue(websiteReports.any { it.websiteId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get website reports by websiteId, given viewer account`()
    {
      // when
      websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.getWebsiteReportsByWebsite(1)
      }
    }

    @Test
    fun `cannot get website reports by websiteId, given no account`()
    {
      // when
      websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.getWebsiteReportsByWebsite(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports with headers, when no parameters, given admin account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 60L))

      // when
      val websiteReports = websiteReportService.getWebsiteReports()

      // then
      websiteReports.page.shouldBe(0)
      websiteReports.size.shouldBe(2)
      websiteReports.totalElements.shouldBe(2)
      websiteReports.totalPages.shouldBe(1)
      websiteReports.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website reports with headers, when with parameters, given admin account`()
    {
      // given
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 40L))
      websiteReportRepository.save(createWebsiteReportEntity(websiteFk = 60L))

      // when
      val websiteReports = websiteReportService.getWebsiteReports(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "id",
          order = OrderEnum.ASC
        )
      )

      // then
      websiteReports.page.shouldBe(1)
      websiteReports.size.shouldBe(1)
      websiteReports.totalElements.shouldBe(2)
      websiteReports.totalPages.shouldBe(2)
      websiteReports.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get WebsiteReport")
  inner class GetWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given admin account`()
    {
      // when
      val expected = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      val actual = websiteReportService.getWebsiteReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given moderator account`()
    {
      // when
      val expected = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      val actual = websiteReportService.getWebsiteReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets website report, given contributor account`()
    {
      // when
      val expected = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      val actual = websiteReportService.getWebsiteReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get website report, given viewer account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.getWebsiteReport(websiteReport.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get websiteReport, when websiteReport not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteReportService.getWebsiteReport(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete WebsiteReport")
  inner class DeleteWebsiteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes website report, given admin account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()
      val websiteReportsBefore = websiteReportService.getWebsiteReports().content

      // then
      Assertions.assertDoesNotThrow {
        websiteReportService.deleteWebsiteReport(websiteReport.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteReportService.getWebsiteReport(websiteReport.id)
      }

      val websiteReportsAfter = websiteReportService.getWebsiteReports().content

      websiteReportsBefore.shouldContain(websiteReport)
      websiteReportsAfter.shouldNotContain(websiteReport)
      websiteReportsBefore.shouldContainAll(websiteReportsAfter)
      websiteReportsAfter.shouldNotContainAll(websiteReportsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website report, given moderator account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.deleteWebsiteReport(websiteReport.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website report, given contributor account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.deleteWebsiteReport(websiteReport.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website report, given viewer account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.deleteWebsiteReport(websiteReport.id)
      }
    }

    @Test
    fun `cannot delete website report, given no account`()
    {
      // when
      val websiteReport = websiteReportRepository.save(createWebsiteReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteReportService.deleteWebsiteReport(websiteReport.id)
      }
    }
  }
}
