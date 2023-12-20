package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageScanEntity
import ch.barrierelos.backend.helper.createWebpageScanModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageScanService
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
abstract class WebpageScanServiceTests : ServiceTests()
{
  @Autowired
  lateinit var webpageScanService: WebpageScanService

  @Nested
  @DisplayName("Add Webpage Scan")
  inner class AddWebpageScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage scan, given admin account`()
    {
      // when
      val expected = createWebpageScanModel()

      // then
      val actual = webpageScanService.addWebpageScan(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.webpageId, actual.webpageId)
      Assertions.assertEquals(expected.webpageStatisticId, actual.webpageStatisticId)
      Assertions.assertEquals(expected.webpageResultId, actual.webpageResultId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage scan, given moderator account`()
    {
      // when
      val webpageScan = createWebpageScanModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.addWebpageScan(webpageScan)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage scan, given contributor account`()
    {
      // when
      val webpageScan = createWebpageScanModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.addWebpageScan(webpageScan)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage scan, given viewer account`()
    {
      // when
      val webpageScan = createWebpageScanModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.addWebpageScan(webpageScan)
      }
    }

    @Test
    fun `cannot add webpageScan, given no account`()
    {
      // when
      val webpageScan = createWebpageScanModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.addWebpageScan(webpageScan)
      }
    }
  }

  @Nested
  @DisplayName("Update Webpage Scan")
  inner class UpdateWebpageScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates webpage scan, given admin account`()
    {
      // when
      val expected = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      val actual = webpageScanService.updateWebpageScan(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.webpageId, actual.webpageId)
      Assertions.assertEquals(expected.webpageStatisticId, actual.webpageStatisticId)
      Assertions.assertEquals(expected.webpageResultId, actual.webpageResultId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage scan, given moderator account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.updateWebpageScan(webpageScan)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage scan, given contributor account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.updateWebpageScan(webpageScan)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage scan, given viewer account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.updateWebpageScan(webpageScan)
      }
    }

    @Test
    fun `cannot update webpage scan, given no account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.updateWebpageScan(webpageScan)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage scan, when webpage scan not exists`()
    {
      // when
      val webpageScan = createWebpageScanModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageScanService.updateWebpageScan(webpageScan)
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Scans")
  inner class GetWebpageScansTests
  {
    @Test
    fun `gets webpage scans`()
    {
      // given
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 40
      })
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 60
      })

      // when
      val webpageScans = webpageScanService.getWebpageScans().content

      // then
      webpageScans.shouldNotBeEmpty()
      webpageScans.shouldHaveSize(2)
      Assertions.assertTrue(webpageScans.any { it.webpageId == 40L })
      Assertions.assertTrue(webpageScans.any { it.webpageId == 60L })
    }

    @Test
    fun `gets webpage scans with headers, when no parameters`()
    {
      // given
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 40
      })
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 60
      })

      // when
      val webpageScans = webpageScanService.getWebpageScans()

      // then
      webpageScans.page.shouldBe(0)
      webpageScans.size.shouldBe(2)
      webpageScans.totalElements.shouldBe(2)
      webpageScans.totalPages.shouldBe(1)
      webpageScans.count.shouldBe(2)
      webpageScans.lastModified.shouldBeGreaterThan(0)
      webpageScans.content.shouldHaveSize(2)
    }

    @Test
    fun `gets webpage scans with headers, when with parameters`()
    {
      // given
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 40
      })
      webpageScanRepository.save(createWebpageScanEntity().apply {
        webpageFk = 60
      })

      // when
      val webpageScans = webpageScanService.getWebpageScans(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "webpageId",
          order = OrderEnum.ASC
        )
      )

      // then
      webpageScans.page.shouldBe(1)
      webpageScans.size.shouldBe(1)
      webpageScans.totalElements.shouldBe(2)
      webpageScans.totalPages.shouldBe(2)
      webpageScans.count.shouldBe(2)
      webpageScans.lastModified.shouldBeGreaterThan(0)
      webpageScans.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Webpage Scan")
  inner class GetWebpageScanTests
  {
    @Test
    fun `gets webpage scan`()
    {
      // when
      val expected = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      val actual = webpageScanService.getWebpageScan(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get webpage scan, when webpage scan not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageScanService.getWebpageScan(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Scan")
  inner class DeleteWebpageScanTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes webpage scan, given admin account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()
      val webpageScansBefore = webpageScanService.getWebpageScans().content

      // then
      Assertions.assertDoesNotThrow {
        webpageScanService.deleteWebpageScan(webpageScan.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageScanService.getWebpageScan(webpageScan.id)
      }

      val webpageScansAfter = webpageScanService.getWebpageScans().content

      webpageScansBefore.shouldContain(webpageScan)
      webpageScansAfter.shouldNotContain(webpageScan)
      webpageScansBefore.shouldContainAll(webpageScansAfter)
      webpageScansAfter.shouldNotContainAll(webpageScansBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage scan, given moderator account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.deleteWebpageScan(webpageScan.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage scan, given contributor account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.deleteWebpageScan(webpageScan.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage scan, given viewer account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.deleteWebpageScan(webpageScan.id)
      }
    }

    @Test
    fun `cannot delete webpageScan, given no account`()
    {
      // when
      val webpageScan = webpageScanRepository.save(createWebpageScanEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageScanService.deleteWebpageScan(webpageScan.id)
      }
    }
  }
}
