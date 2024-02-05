package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createUserReportEntity
import ch.barrierelos.backend.helper.createUserReportModel
import ch.barrierelos.backend.service.UserReportService
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
abstract class UserReportServiceTests : ServiceTests()
{
  @Autowired
  lateinit var userReportService: UserReportService

  @Nested
  @DisplayName("Add User Report")
  inner class AddUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds user report, given admin account`()
    {
      // when
      val expected = createUserReportModel()

      // then
      val actual = userReportService.addUserReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds user report, given moderator account`()
    {
      // when
      val expected = createUserReportModel()

      // then
      val actual = userReportService.addUserReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds user report, given contributor account`()
    {
      // when
      val expected = createUserReportModel()

      // then
      val actual = userReportService.addUserReport(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertNotEquals(0, actual.report.id)
      Assertions.assertEquals(expected.report.userId, actual.report.userId)
      Assertions.assertEquals(expected.report.reason, actual.report.reason)
      Assertions.assertEquals(expected.report.state, actual.report.state)
      Assertions.assertEquals(expected.report.modified, actual.report.modified)
      Assertions.assertEquals(expected.report.created, actual.report.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add user report, given viewer account`()
    {
      // when
      val userReport = createUserReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.addUserReport(userReport)
      }
    }

    @Test
    fun `cannot add user report, given no account`()
    {
      // when
      val userReport = createUserReportModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.addUserReport(userReport)
      }
    }
  }

  @Nested
  @DisplayName("Get User Reports")
  inner class GetUserReportsTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports, given admin account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 60L))

      // when
      val userReports = userReportService.getUserReports().content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
      Assertions.assertTrue(userReports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports, given moderator account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 60L))

      // when
      val userReports = userReportService.getUserReports().content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
      Assertions.assertTrue(userReports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports, given contributor account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 60L))

      // when
      val userReports = userReportService.getUserReports().content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
      Assertions.assertTrue(userReports.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get user reports, given viewer account`()
    {
      // when
      userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.getUserReports()
      }
    }

    @Test
    fun `cannot get user reports, given no account`()
    {
      // when
      userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.getUserReports()
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given admin account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 40L))

      // when
      val userReports = userReportService.getUserReportsByUser(40).content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given moderator account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 40L))

      // when
      val userReports = userReportService.getUserReportsByUser(40).content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports by userId, given contributor account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 40L))

      // when
      val userReports = userReportService.getUserReportsByUser(40).content

      // then
      userReports.shouldNotBeEmpty()
      userReports.shouldHaveSize(2)
      Assertions.assertTrue(userReports.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get user reports by userId, given viewer account`()
    {
      // when
      userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.getUserReportsByUser(1)
      }
    }

    @Test
    fun `cannot get user reports by userId, given no account`()
    {
      // when
      userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.getUserReportsByUser(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports with headers, when no parameters, given admin account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 60L))

      // when
      val userReports = userReportService.getUserReports()

      // then
      userReports.page.shouldBe(0)
      userReports.size.shouldBe(2)
      userReports.totalElements.shouldBe(2)
      userReports.totalPages.shouldBe(1)
      userReports.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user reports with headers, when with parameters, given admin account`()
    {
      // given
      userReportRepository.save(createUserReportEntity(userFk = 40L))
      userReportRepository.save(createUserReportEntity(userFk = 60L))

      // when
      val userReports = userReportService.getUserReports(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "id",
          order = OrderEnum.ASC
        )
      )

      // then
      userReports.page.shouldBe(1)
      userReports.size.shouldBe(1)
      userReports.totalElements.shouldBe(2)
      userReports.totalPages.shouldBe(2)
      userReports.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get User Report")
  inner class GetUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given admin account`()
    {
      // when
      val expected = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      val actual = userReportService.getUserReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given moderator account`()
    {
      // when
      val expected = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      val actual = userReportService.getUserReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user report, given contributor account`()
    {
      // when
      val expected = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      val actual = userReportService.getUserReport(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get user report, given viewer account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.getUserReport(userReport.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get userReport, when userReport not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        userReportService.getUserReport(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete User Report")
  inner class DeleteUserReportTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes user report, given admin account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()
      val userReportsBefore = userReportService.getUserReports().content

      // then
      Assertions.assertDoesNotThrow {
        userReportService.deleteUserReport(userReport.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        userReportService.getUserReport(userReport.id)
      }

      val userReportsAfter = userReportService.getUserReports().content

      userReportsBefore.shouldContain(userReport)
      userReportsAfter.shouldNotContain(userReport)
      userReportsBefore.shouldContainAll(userReportsAfter)
      userReportsAfter.shouldNotContainAll(userReportsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete user report, given moderator account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.deleteUserReport(userReport.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete user report, given contributor account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.deleteUserReport(userReport.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete user report, given viewer account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.deleteUserReport(userReport.id)
      }
    }

    @Test
    fun `cannot delete user report, given no account`()
    {
      // when
      val userReport = userReportRepository.save(createUserReportEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userReportService.deleteUserReport(userReport.id)
      }
    }
  }
}
