package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteStatisticEntity
import ch.barrierelos.backend.helper.createWebsiteStatisticModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteStatisticService
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
abstract class WebsiteStatisticServiceTests : ServiceTests()
{
  @Autowired
  lateinit var websiteStatisticService: WebsiteStatisticService

  @Nested
  @DisplayName("Add Website Statistic")
  inner class AddWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds website statistic, given admin account`()
    {
      // when
      val expected = createWebsiteStatisticModel()

      // then
      val actual = websiteStatisticService.addWebsiteStatistic(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.score, actual.score)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website statistic, given moderator account`()
    {
      // when
      val websiteStatistic = createWebsiteStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.addWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website statistic, given contributor account`()
    {
      // when
      val websiteStatistic = createWebsiteStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.addWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website statistic, given viewer account`()
    {
      // when
      val websiteStatistic = createWebsiteStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.addWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    fun `cannot add websiteStatistic, given no account`()
    {
      // when
      val websiteStatistic = createWebsiteStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.addWebsiteStatistic(websiteStatistic)
      }
    }
  }

  @Nested
  @DisplayName("Update Website Statistic")
  inner class UpdateWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates website statistic, given admin account`()
    {
      // when
      val expected = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()
      expected.score = 20.0

      // then
      val actual = websiteStatisticService.updateWebsiteStatistic(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.score, actual.score)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website statistic, given moderator account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website statistic, given contributor account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website statistic, given viewer account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    fun `cannot update website statistic, given no account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website statistic, when website statistic not exists`()
    {
      // when
      val websiteStatistic = createWebsiteStatisticModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
      }
    }
  }

  @Nested
  @DisplayName("Get Website Statistics")
  inner class GetWebsiteStatisticsTests
  {
    @Test
    fun `gets website statistics`()
    {
      // given
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 40.0
      })
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 60.0
      })

      // when
      val websiteStatistics = websiteStatisticService.getWebsiteStatistics().content

      // then
      websiteStatistics.shouldNotBeEmpty()
      websiteStatistics.shouldHaveSize(2)
      Assertions.assertTrue(websiteStatistics.any { it.score == 40.0 })
      Assertions.assertTrue(websiteStatistics.any { it.score == 60.0 })
    }

    @Test
    fun `gets website statistics with headers, when no parameters`()
    {
      // given
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 40.0
      })
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 60.0
      })

      // when
      val websiteStatistics = websiteStatisticService.getWebsiteStatistics()

      // then
      websiteStatistics.page.shouldBe(0)
      websiteStatistics.size.shouldBe(2)
      websiteStatistics.totalElements.shouldBe(2)
      websiteStatistics.totalPages.shouldBe(1)
      websiteStatistics.count.shouldBe(2)
      websiteStatistics.lastModified.shouldBeGreaterThan(0)
      websiteStatistics.content.shouldHaveSize(2)
    }

    @Test
    fun `gets website statistics with headers, when with parameters`()
    {
      // given
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 40.0
      })
      websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
        score = 60.0
      })

      // when
      val websiteStatistics = websiteStatisticService.getWebsiteStatistics(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "score",
          order = OrderEnum.ASC
        )
      )

      // then
      websiteStatistics.page.shouldBe(1)
      websiteStatistics.size.shouldBe(1)
      websiteStatistics.totalElements.shouldBe(2)
      websiteStatistics.totalPages.shouldBe(2)
      websiteStatistics.count.shouldBe(2)
      websiteStatistics.lastModified.shouldBeGreaterThan(0)
      websiteStatistics.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Website Statistic")
  inner class GetWebsiteStatisticTests
  {
    @Test
    fun `gets website statistic`()
    {
      // when
      val expected = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      val actual = websiteStatisticService.getWebsiteStatistic(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get website statistic, when website statistic not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteStatisticService.getWebsiteStatistic(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Website Statistic")
  inner class DeleteWebsiteStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes website statistic, given admin account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()
      val websiteStatisticsBefore = websiteStatisticService.getWebsiteStatistics().content

      // then
      Assertions.assertDoesNotThrow {
        websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteStatisticService.getWebsiteStatistic(websiteStatistic.id)
      }

      val websiteStatisticsAfter = websiteStatisticService.getWebsiteStatistics().content

      websiteStatisticsBefore.shouldContain(websiteStatistic)
      websiteStatisticsAfter.shouldNotContain(websiteStatistic)
      websiteStatisticsBefore.shouldContainAll(websiteStatisticsAfter)
      websiteStatisticsAfter.shouldNotContainAll(websiteStatisticsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website statistic, given moderator account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website statistic, given contributor account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website statistic, given viewer account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
      }
    }

    @Test
    fun `cannot delete websiteStatistic, given no account`()
    {
      // when
      val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
      }
    }
  }
}
