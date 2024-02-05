package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createReportEntity
import ch.barrierelos.backend.helper.createReportModel
import ch.barrierelos.backend.service.ReportService
import io.kotest.matchers.collections.*
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class ReportServiceTests : ServiceTests()
{
  @Autowired
  lateinit var reportService: ReportService

  @Nested
  @DisplayName("Add Report")
  inner class AddReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report, given admin account`()
    {
      // when
      val expected = createReportModel()

      // then
      val actual = reportService.addReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report, given moderator account`()
    {
      // when
      val expected = createReportModel()

      // then
      val actual = reportService.addReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report, given contributor account`()
    {
      // when
      val expected = createReportModel()

      // then
      val actual = reportService.addReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add report, given viewer account`()
    {
      // when
      val report = createReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.addReport(report)
      }
    }

    @Test
    fun `cannot add report, given no account`()
    {
      // when
      val report = createReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.addReport(report)
      }
    }
  }

  @Nested
  @DisplayName("Update Report")
  inner class UpdateReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates report, given admin account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity()).toModel()
      expected.state = StateEnum.CLOSED

      // then
      val actual = reportService.updateReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates report, given moderator account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity()).toModel()
      expected.state = StateEnum.CLOSED

      // then
      val actual = reportService.updateReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report, given contributor account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.updateReport(report)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report, given viewer account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.updateReport(report)
      }
    }

    @Test
    fun `cannot update report, given no account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.updateReport(report)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report, when report not exists`()
    {
      // when
      val report = createReportModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportService.updateReport(report)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates report, when changing reason, given admin account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity().apply { reason = ReasonEnum.INCORRECT }).toModel()
      expected.reason = ReasonEnum.INAPPROPRIATE

      // then
      val actual = reportService.updateReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.reason, actual.reason)
      Assertions.assertEquals(expected.state, actual.state)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report, when changing reason, given moderator account`()
    {
      // when
      val report = reportRepository.save(createReportEntity().apply { reason = ReasonEnum.INCORRECT }).toModel()
      report.reason = ReasonEnum.INAPPROPRIATE

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        reportService.updateReport(report)
      }
    }
  }

  @Nested
  @DisplayName("Get Reports")
  inner class GetReportsTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports, given admin account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 60L))

      // when
      val reports = reportService.getReports().content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
      Assertions.assertTrue(reports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports, given moderator account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 60L))

      // when
      val reports = reportService.getReports().content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
      Assertions.assertTrue(reports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports, given contributor account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 60L))

      // when
      val reports = reportService.getReports().content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
      Assertions.assertTrue(reports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get reports, given viewer account`()
    {
      // when
      reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.getReports()
      }
    }

    @Test
    fun `cannot get reports, given no account`()
    {
      // when
      reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.getReports()
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given admin account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 40L))

      // when
      val reports = reportService.getReportsByUser(40).content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given moderator account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 40L))

      // when
      val reports = reportService.getReportsByUser(40).content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports by userId, given contributor account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 40L))

      // when
      val reports = reportService.getReportsByUser(40).content

      // then
      reports.shouldNotBeEmpty()
      reports.shouldHaveSize(2)
      Assertions.assertTrue(reports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get reports by userId, given viewer account`()
    {
      // when
      reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.getReportsByUser(1)
      }
    }

    @Test
    fun `cannot get reports by userId, given no account`()
    {
      // when
      reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.getReportsByUser(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports with headers, when no parameters, given admin account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 60L))

      // when
      val reports = reportService.getReports()

      // then
      reports.page.shouldBe(0)
      reports.size.shouldBe(2)
      reports.totalElements.shouldBe(2)
      reports.totalPages.shouldBe(1)
      reports.lastModified.shouldBeGreaterThan(0)
      reports.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets reports with headers, when with parameters, given admin account`()
    {
      // given
      reportRepository.save(createReportEntity(userFk = 40L))
      reportRepository.save(createReportEntity(userFk = 60L))

      // when
      val reports = reportService.getReports(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "id",
          order = OrderEnum.ASC
        )
      )

      // then
      reports.page.shouldBe(1)
      reports.size.shouldBe(1)
      reports.totalElements.shouldBe(2)
      reports.totalPages.shouldBe(2)
      reports.lastModified.shouldBeGreaterThan(0)
      reports.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Report")
  inner class GetReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given admin account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity()).toModel()

      // then
      val actual = reportService.getReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given moderator account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity()).toModel()

      // then
      val actual = reportService.getReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report, given contributor account`()
    {
      // when
      val expected = reportRepository.save(createReportEntity()).toModel()

      // then
      val actual = reportService.getReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get report, given viewer account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.getReport(report.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get report, when report not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportService.getReport(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Report")
  inner class DeleteReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes report, given admin account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()
      val reportsBefore = reportService.getReports().content

      // then
      Assertions.assertDoesNotThrow {
        reportService.deleteReport(report.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportService.getReport(report.id)
      }

      val reportsAfter = reportService.getReports().content

      reportsBefore.shouldContain(report)
      reportsAfter.shouldNotContain(report)
      reportsBefore.shouldContainAll(reportsAfter)
      reportsAfter.shouldNotContainAll(reportsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report, given moderator account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.deleteReport(report.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report, given contributor account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.deleteReport(report.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report, given viewer account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.deleteReport(report.id)
      }
    }

    @Test
    fun `cannot delete report, given no account`()
    {
      // when
      val report = reportRepository.save(createReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportService.deleteReport(report.id)
      }
    }
  }
}
