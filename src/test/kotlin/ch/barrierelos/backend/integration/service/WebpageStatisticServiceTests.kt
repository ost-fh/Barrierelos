package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageStatisticEntity
import ch.barrierelos.backend.helper.createWebpageStatisticModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageStatisticService
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
abstract class WebpageStatisticServiceTests : ServiceTests()
{
  @Autowired
  lateinit var webpageStatisticService: WebpageStatisticService

  @Nested
  @DisplayName("Add Webpage Statistic")
  inner class AddWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage statistic, given admin account`()
    {
      // when
      val expected = createWebpageStatisticModel()

      // then
      val actual = webpageStatisticService.addWebpageStatistic(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.score, actual.score)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage statistic, given moderator account`()
    {
      // when
      val webpageStatistic = createWebpageStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.addWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage statistic, given contributor account`()
    {
      // when
      val webpageStatistic = createWebpageStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.addWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage statistic, given viewer account`()
    {
      // when
      val webpageStatistic = createWebpageStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.addWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    fun `cannot add webpageStatistic, given no account`()
    {
      // when
      val webpageStatistic = createWebpageStatisticModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.addWebpageStatistic(webpageStatistic)
      }
    }
  }

  @Nested
  @DisplayName("Update Webpage Statistic")
  inner class UpdateWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates webpage statistic, given admin account`()
    {
      // when
      val expected = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()
      expected.score = 20.0

      // then
      val actual = webpageStatisticService.updateWebpageStatistic(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.score, actual.score)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage statistic, given moderator account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.updateWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage statistic, given contributor account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.updateWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage statistic, given viewer account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.updateWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    fun `cannot update webpage statistic, given no account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.updateWebpageStatistic(webpageStatistic)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage statistic, when webpage statistic not exists`()
    {
      // when
      val webpageStatistic = createWebpageStatisticModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageStatisticService.updateWebpageStatistic(webpageStatistic)
      }
    }
  }

  @Nested
  @DisplayName("Get Webpage Statistics")
  inner class GetWebpageStatisticsTests
  {
    @Test
    fun `gets webpage statistics`()
    {
      // given
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 40.0
      })
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 60.0
      })

      // when
      val webpageStatistics = webpageStatisticService.getWebpageStatistics().content

      // then
      webpageStatistics.shouldNotBeEmpty()
      webpageStatistics.shouldHaveSize(2)
      Assertions.assertTrue(webpageStatistics.any { it.score == 40.0 })
      Assertions.assertTrue(webpageStatistics.any { it.score == 60.0 })
    }

    @Test
    fun `gets webpage statistics with headers, when no parameters`()
    {
      // given
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 40.0
      })
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 60.0
      })

      // when
      val webpageStatistics = webpageStatisticService.getWebpageStatistics()

      // then
      webpageStatistics.page.shouldBe(0)
      webpageStatistics.size.shouldBe(2)
      webpageStatistics.totalElements.shouldBe(2)
      webpageStatistics.totalPages.shouldBe(1)
      webpageStatistics.count.shouldBe(2)
      webpageStatistics.lastModified.shouldBeGreaterThan(0)
      webpageStatistics.content.shouldHaveSize(2)
    }

    @Test
    fun `gets webpage statistics with headers, when with parameters`()
    {
      // given
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 40.0
      })
      webpageStatisticRepository.save(createWebpageStatisticEntity().apply {
        score = 60.0
      })

      // when
      val webpageStatistics = webpageStatisticService.getWebpageStatistics(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "score",
          order = OrderEnum.ASC
        )
      )

      // then
      webpageStatistics.page.shouldBe(1)
      webpageStatistics.size.shouldBe(1)
      webpageStatistics.totalElements.shouldBe(2)
      webpageStatistics.totalPages.shouldBe(2)
      webpageStatistics.count.shouldBe(2)
      webpageStatistics.lastModified.shouldBeGreaterThan(0)
      webpageStatistics.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Webpage Statistic")
  inner class GetWebpageStatisticTests
  {
    @Test
    fun `gets webpage statistic`()
    {
      // when
      val expected = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      val actual = webpageStatisticService.getWebpageStatistic(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get webpage statistic, when webpage statistic not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageStatisticService.getWebpageStatistic(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage Statistic")
  inner class DeleteWebpageStatisticTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes webpage statistic, given admin account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()
      val webpageStatisticsBefore = webpageStatisticService.getWebpageStatistics().content

      // then
      Assertions.assertDoesNotThrow {
        webpageStatisticService.deleteWebpageStatistic(webpageStatistic.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageStatisticService.getWebpageStatistic(webpageStatistic.id)
      }

      val webpageStatisticsAfter = webpageStatisticService.getWebpageStatistics().content

      webpageStatisticsBefore.shouldContain(webpageStatistic)
      webpageStatisticsAfter.shouldNotContain(webpageStatistic)
      webpageStatisticsBefore.shouldContainAll(webpageStatisticsAfter)
      webpageStatisticsAfter.shouldNotContainAll(webpageStatisticsBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage statistic, given moderator account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.deleteWebpageStatistic(webpageStatistic.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage statistic, given contributor account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.deleteWebpageStatistic(webpageStatistic.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage statistic, given viewer account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.deleteWebpageStatistic(webpageStatistic.id)
      }
    }

    @Test
    fun `cannot delete webpageStatistic, given no account`()
    {
      // when
      val webpageStatistic = webpageStatisticRepository.save(createWebpageStatisticEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageStatisticService.deleteWebpageStatistic(webpageStatistic.id)
      }
    }
  }
}
