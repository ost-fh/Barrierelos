package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageReportEntity
import ch.barrierelos.backend.helper.createWebpageReportModel
import ch.barrierelos.backend.service.WebpageReportService
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
abstract class WebpageReportServiceTests : ServiceTests()
{
  @Autowired
  lateinit var webpageReportService: WebpageReportService

  @Nested
  @DisplayName("Add Webpage Report")
  inner class AddWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage report, given admin account`()
    {
      // when
      val expected = createWebpageReportModel()

      // then
      val actual = webpageReportService.addWebpageReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.webpageId, actual.webpageId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage report, given moderator account`()
    {
      // when
      val expected = createWebpageReportModel()

      // then
      val actual = webpageReportService.addWebpageReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.webpageId, actual.webpageId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage report, given contributor account`()
    {
      // when
      val expected = createWebpageReportModel()

      // then
      val actual = webpageReportService.addWebpageReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.webpageId, actual.webpageId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage report, given viewer account`()
    {
      // when
      val webpageReport = createWebpageReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.addWebpageReport(webpageReport)
      }
    }

    @Test
    fun `cannot add webpage report, given no account`()
    {
      // when
      val webpageReport = createWebpageReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.addWebpageReport(webpageReport)
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Reports")
  inner class GetWebpageReportsTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given admin account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 60L))

      // when
      val webpageReports = webpageReportService.getWebpageReports().content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
      Assertions.assertTrue(webpageReports.any { it.webpageId == 60L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given moderator account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 60L))

      // when
      val webpageReports = webpageReportService.getWebpageReports().content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
      Assertions.assertTrue(webpageReports.any { it.webpageId == 60L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports, given contributor account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 60L))

      // when
      val webpageReports = webpageReportService.getWebpageReports().content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
      Assertions.assertTrue(webpageReports.any { it.webpageId == 60L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get webpage reports, given viewer account`()
    {
      // when
      webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.getWebpageReports()
      }
    }

    @Test
    fun `cannot get webpage reports, given no account`()
    {
      // when
      webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.getWebpageReports()
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given admin account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))

      // when
      val webpageReports = webpageReportService.getWebpageReportsByWebpage(40).content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given moderator account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))

      // when
      val webpageReports = webpageReportService.getWebpageReportsByWebpage(40).content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports by webpageId, given contributor account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))

      // when
      val webpageReports = webpageReportService.getWebpageReportsByWebpage(40).content

      // then
      webpageReports.shouldNotBeEmpty()
      webpageReports.shouldHaveSize(2)
      Assertions.assertTrue(webpageReports.any { it.webpageId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get webpage reports by webpageId, given viewer account`()
    {
      // when
      webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.getWebpageReportsByWebpage(1)
      }
    }

    @Test
    fun `cannot get webpage reports by webpageId, given no account`()
    {
      // when
      webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.getWebpageReportsByWebpage(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports with headers, when no parameters, given admin account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 60L))

      // when
      val webpageReports = webpageReportService.getWebpageReports()

      // then
      webpageReports.page.shouldBe(0)
      webpageReports.size.shouldBe(2)
      webpageReports.totalElements.shouldBe(2)
      webpageReports.totalPages.shouldBe(1)
      webpageReports.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage reports with headers, when with parameters, given admin account`()
    {
      // given
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 40L))
      webpageReportRepository.save(createWebpageReportEntity(webpageFk = 60L))

      // when
      val webpageReports = webpageReportService.getWebpageReports(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "id",
          order = OrderEnum.ASC
        )
      )

      // then
      webpageReports.page.shouldBe(1)
      webpageReports.size.shouldBe(1)
      webpageReports.totalElements.shouldBe(2)
      webpageReports.totalPages.shouldBe(2)
      webpageReports.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Webpage Report")
  inner class GetWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given admin account`()
    {
      // when
      val expected = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      val actual = webpageReportService.getWebpageReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given moderator account`()
    {
      // when
      val expected = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      val actual = webpageReportService.getWebpageReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets webpage report, given contributor account`()
    {
      // when
      val expected = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      val actual = webpageReportService.getWebpageReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get webpage report, given viewer account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.getWebpageReport(webpageReport.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get webpageReport, when webpageReport not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageReportService.getWebpageReport(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Report")
  inner class DeleteWebpageReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes webpage report, given admin account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()
      val webpageReportsBefore = webpageReportService.getWebpageReports().content

      // then
      Assertions.assertDoesNotThrow {
        webpageReportService.deleteWebpageReport(webpageReport.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageReportService.getWebpageReport(webpageReport.id)
      }

      val webpageReportsAfter = webpageReportService.getWebpageReports().content

      webpageReportsBefore.shouldContain(webpageReport)
      webpageReportsAfter.shouldNotContain(webpageReport)
      webpageReportsBefore.shouldContainAll(webpageReportsAfter)
      webpageReportsAfter.shouldNotContainAll(webpageReportsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage report, given moderator account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.deleteWebpageReport(webpageReport.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage report, given contributor account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.deleteWebpageReport(webpageReport.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage report, given viewer account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.deleteWebpageReport(webpageReport.id)
      }
    }

    @Test
    fun `cannot delete webpage report, given no account`()
    {
      // when
      val webpageReport = webpageReportRepository.save(createWebpageReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageReportService.deleteWebpageReport(webpageReport.id)
      }
    }
  }
}
