package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.service.WebsiteScanService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired

// TODO: Add tests for website scan service
@Nested
abstract class WebsiteScanServiceTests : ServiceTests()
{
  @Autowired
  lateinit var websiteScanService: WebsiteScanService

  @Nested
  @DisplayName("Add Website Scan")
  inner class AddWebsiteScanTests
//  {
//    @Test
//    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `adds website scan, given admin account`()
//    {
//      // when
//      val expected = createWebsiteScanModel()
//
//      // then
//      val actual = websiteScanService.addWebsiteScan(expected.copy())
//
//      Assertions.assertNotEquals(0, actual.id)
//      Assertions.assertEquals(expected.website, actual.website)
//      Assertions.assertEquals(expected.websiteStatistic, actual.websiteStatistic)
//      Assertions.assertEquals(expected.websiteResult, actual.websiteResult)
//      Assertions.assertEquals(expected.webpageScans, actual.webpageScans)
//      Assertions.assertNotEquals(expected.modified, actual.modified)
//      Assertions.assertNotEquals(expected.created, actual.created)
//    }
//
//    @Test
//    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot add website scan, given moderator account`()
//    {
//      // when
//      val websiteScan = createWebsiteScanModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.addWebsiteScan(websiteScan)
//      }
//    }
//
//    @Test
//    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot add website scan, given contributor account`()
//    {
//      // when
//      val websiteScan = createWebsiteScanModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.addWebsiteScan(websiteScan)
//      }
//    }
//
//    @Test
//    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot add website scan, given viewer account`()
//    {
//      // when
//      val websiteScan = createWebsiteScanModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.addWebsiteScan(websiteScan)
//      }
//    }
//
//    @Test
//    fun `cannot add websiteScan, given no account`()
//    {
//      // when
//      val websiteScan = createWebsiteScanModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.addWebsiteScan(websiteScan)
//      }
//    }
//  }

  @Nested
  @DisplayName("Get Website Scans")
  inner class GetWebsiteScansTests
//  {
//    @Test
//    fun `gets website scans`()
//    {
//      // given
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 40
//      })
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 60
//      })
//
//      // when
//      val websiteScans = websiteScanService.getWebsiteScans().content
//
//      // then
//      websiteScans.shouldNotBeEmpty()
//      websiteScans.shouldHaveSize(2)
//      Assertions.assertTrue(websiteScans.any { it.website.id == 40L })
//      Assertions.assertTrue(websiteScans.any { it.website.id == 60L })
//    }
//
//    @Test
//    fun `gets website scans with headers, when no parameters`()
//    {
//      // given
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 40
//      })
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 60
//      })
//
//      // when
//      val websiteScans = websiteScanService.getWebsiteScans()
//
//      // then
//      websiteScans.page.shouldBe(0)
//      websiteScans.size.shouldBe(2)
//      websiteScans.totalElements.shouldBe(2)
//      websiteScans.totalPages.shouldBe(1)
//      websiteScans.count.shouldBe(2)
//      websiteScans.lastModified.shouldBeGreaterThan(0)
//      websiteScans.content.shouldHaveSize(2)
//    }
//
//    @Test
//    fun `gets website scans with headers, when with parameters`()
//    {
//      // given
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 40
//      })
//      websiteScanRepository.save(createWebsiteScanEntity().apply {
//        website.websiteId = 60
//      })
//
//      // when
//      val websiteScans = websiteScanService.getWebsiteScans(
//        DefaultParameters(
//          page = 1,
//          size = 1,
//          sort = "websiteId",
//          order = OrderEnum.ASC
//        )
//      )
//
//      // then
//      websiteScans.page.shouldBe(1)
//      websiteScans.size.shouldBe(1)
//      websiteScans.totalElements.shouldBe(2)
//      websiteScans.totalPages.shouldBe(2)
//      websiteScans.count.shouldBe(2)
//      websiteScans.lastModified.shouldBeGreaterThan(0)
//      websiteScans.content.shouldHaveSize(1)
//    }
//  }

  @Nested
  @DisplayName("Get Website Scan")
  inner class GetWebsiteScanTests
//  {
//    @Test
//    fun `gets website scan`()
//    {
//      // when
//      val expected = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//
//      // then
//      val actual = websiteScanService.getWebsiteScan(expected.id)
//
//      Assertions.assertEquals(expected, actual)
//    }
//
//    @Test
//    fun `cannot get website scan, when website scan not exists`()
//    {
//      Assertions.assertThrows(NoSuchElementException::class.java) {
//        websiteScanService.getWebsiteScan(5000000)
//      }
//    }
//  }

  @Nested
  @DisplayName("Delete Website Scan")
  inner class DeleteWebsiteScanTests
//  {
//    @Test
//    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `deletes website scan, given admin account`()
//    {
//      // when
//      val websiteScan = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//      val websiteScansBefore = websiteScanService.getWebsiteScans().content
//
//      // then
//      Assertions.assertDoesNotThrow {
//        websiteScanService.deleteWebsiteScan(websiteScan.id)
//      }
//
//      Assertions.assertThrows(NoSuchElementException::class.java) {
//        websiteScanService.getWebsiteScan(websiteScan.id)
//      }
//
//      val websiteScansAfter = websiteScanService.getWebsiteScans().content
//
//      websiteScansBefore.shouldContain(websiteScan)
//      websiteScansAfter.shouldNotContain(websiteScan)
//      websiteScansBefore.shouldContainAll(websiteScansAfter)
//      websiteScansAfter.shouldNotContainAll(websiteScansBefore)
//    }
//
//    @Test
//    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot delete website scan, given moderator account`()
//    {
//      // when
//      val websiteScan = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.deleteWebsiteScan(websiteScan.id)
//      }
//    }
//
//    @Test
//    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot delete website scan, given contributor account`()
//    {
//      // when
//      val websiteScan = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.deleteWebsiteScan(websiteScan.id)
//      }
//    }
//
//    @Test
//    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
//    fun `cannot delete website scan, given viewer account`()
//    {
//      // when
//      val websiteScan = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.deleteWebsiteScan(websiteScan.id)
//      }
//    }
//
//    @Test
//    fun `cannot delete websiteScan, given no account`()
//    {
//      // when
//      val websiteScan = websiteScanRepository.save(createWebsiteScanEntity()).toModel()
//
//      // then
//      Assertions.assertThrows(NoAuthorizationException::class.java) {
//        websiteScanService.deleteWebsiteScan(websiteScan.id)
//      }
//    }
//  }
}
