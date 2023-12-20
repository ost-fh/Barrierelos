package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createReportMessageEntity
import ch.barrierelos.backend.helper.createReportMessageModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.ReportMessageService
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
abstract class ReportMessageServiceTests : ServiceTests()
{
  @Autowired
  lateinit var reportMessageService: ReportMessageService

  @Nested
  @DisplayName("Add Report Message")
  inner class AddReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report message, given admin account`()
    {
      // when
      val expected = createReportMessageModel()

      // then
      val actual = reportMessageService.addReportMessage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.reportId, actual.reportId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.message, actual.message)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report message, given moderator account`()
    {
      // when
      val expected = createReportMessageModel()

      // then
      val actual = reportMessageService.addReportMessage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.reportId, actual.reportId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.message, actual.message)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds report message, given contributor account`()
    {
      // when
      val expected = createReportMessageModel()

      // then
      val actual = reportMessageService.addReportMessage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.reportId, actual.reportId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.message, actual.message)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add report message, given viewer account`()
    {
      // when
      val reportMessage = createReportMessageModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.addReportMessage(reportMessage)
      }
    }

    @Test
    fun `cannot add report message, given no account`()
    {
      // when
      val reportMessage = createReportMessageModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.addReportMessage(reportMessage)
      }
    }
  }

  @Nested
  @DisplayName("Update Report Message")
  inner class UpdateReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates report message, given admin account`()
    {
      // when
      val expected = reportMessageRepository.save(createReportMessageEntity()).toModel()
      expected.message = "message"

      // then
      val actual = reportMessageService.updateReportMessage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.reportId, actual.reportId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.message, actual.message)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report message, given moderator account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.updateReportMessage(reportMessage)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report message, given contributor account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.updateReportMessage(reportMessage)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report message, given viewer account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.updateReportMessage(reportMessage)
      }
    }

    @Test
    fun `cannot update report message, given no account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.updateReportMessage(reportMessage)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update report message, when report message not exists`()
    {
      // when
      val reportMessage = createReportMessageModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportMessageService.updateReportMessage(reportMessage)
      }
    }
  }

  @Nested
  @DisplayName("Get Report Messages")
  inner class GetReportMessagesTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages, given admin account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 60L))

      // when
      val reportMessages = reportMessageService.getReportMessages().content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
      Assertions.assertTrue(reportMessages.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages, given moderator account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 60L))

      // when
      val reportMessages = reportMessageService.getReportMessages().content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
      Assertions.assertTrue(reportMessages.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages, given contributor account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 60L))

      // when
      val reportMessages = reportMessageService.getReportMessages().content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
      Assertions.assertTrue(reportMessages.any { it.userId == 60L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get report messages, given viewer account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessages()
      }
    }

    @Test
    fun `cannot get report messages, given no account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessages()
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given admin account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByReport(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.reportId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given moderator account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByReport(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.reportId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by reportId, given contributor account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(reportFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByReport(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.reportId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get reportMessages by reportId, given viewer account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessagesByReport(1)
      }
    }

    @Test
    fun `cannot get reportMessages by reportId, given no account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessagesByReport(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given admin account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByUser(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given moderator account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByUser(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages by userId, given contributor account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))

      // when
      val reportMessages = reportMessageService.getReportMessagesByUser(40).content

      // then
      reportMessages.shouldNotBeEmpty()
      reportMessages.shouldHaveSize(2)
      Assertions.assertTrue(reportMessages.any { it.userId == 40L })
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get reportMessages by userId, given viewer account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessagesByUser(1)
      }
    }

    @Test
    fun `cannot get reportMessages by userId, given no account`()
    {
      // when
      reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessagesByUser(1)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages with headers, when no parameters, given admin account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 60L))

      // when
      val reportMessages = reportMessageService.getReportMessages()

      // then
      reportMessages.page.shouldBe(0)
      reportMessages.size.shouldBe(2)
      reportMessages.totalElements.shouldBe(2)
      reportMessages.totalPages.shouldBe(1)
      reportMessages.count.shouldBe(2)
      reportMessages.lastModified.shouldBeGreaterThan(0)
      reportMessages.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report messages with headers, when with parameters, given admin account`()
    {
      // given
      reportMessageRepository.save(createReportMessageEntity(userFk = 40L))
      reportMessageRepository.save(createReportMessageEntity(userFk = 60L))

      // when
      val reportMessages = reportMessageService.getReportMessages(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "id",
          order = OrderEnum.ASC
        )
      )

      // then
      reportMessages.page.shouldBe(1)
      reportMessages.size.shouldBe(1)
      reportMessages.totalElements.shouldBe(2)
      reportMessages.totalPages.shouldBe(2)
      reportMessages.count.shouldBe(2)
      reportMessages.lastModified.shouldBeGreaterThan(0)
      reportMessages.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Report Message")
  inner class GetReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given admin account`()
    {
      // when
      val expected = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      val actual = reportMessageService.getReportMessage(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given moderator account`()
    {
      // when
      val expected = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      val actual = reportMessageService.getReportMessage(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets report message, given contributor account`()
    {
      // when
      val expected = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      val actual = reportMessageService.getReportMessage(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get report message, given viewer account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.getReportMessage(reportMessage.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get report message, when report message not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportMessageService.getReportMessage(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Report Message")
  inner class DeleteReportMessageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes report message, given admin account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()
      val reportMessagesBefore = reportMessageService.getReportMessages().content

      // then
      Assertions.assertDoesNotThrow {
        reportMessageService.deleteReportMessage(reportMessage.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        reportMessageService.getReportMessage(reportMessage.id)
      }

      val reportMessagesAfter = reportMessageService.getReportMessages().content

      reportMessagesBefore.shouldContain(reportMessage)
      reportMessagesAfter.shouldNotContain(reportMessage)
      reportMessagesBefore.shouldContainAll(reportMessagesAfter)
      reportMessagesAfter.shouldNotContainAll(reportMessagesBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report message, given moderator account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.deleteReportMessage(reportMessage.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report message, given contributor account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.deleteReportMessage(reportMessage.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete report message, given viewer account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.deleteReportMessage(reportMessage.id)
      }
    }

    @Test
    fun `cannot delete report message, given no account`()
    {
      // when
      val reportMessage = reportMessageRepository.save(createReportMessageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        reportMessageService.deleteReportMessage(reportMessage.id)
      }
    }
  }
}
