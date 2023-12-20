package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.InvalidPathException
import ch.barrierelos.backend.exceptions.InvalidUrlException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebpageEntity
import ch.barrierelos.backend.helper.createWebpageModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebpageService
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
abstract class WebpageServiceTests : ServiceTests()
{
  @Autowired
  lateinit var webpageService: WebpageService

  @Nested
  @DisplayName("Add Webpage")
  inner class AddWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds webpage, given admin account`()
    {
      // when
      val expected = createWebpageModel(1, 0)

      // then
      val actual = webpageService.addWebpage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.path, actual.path)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage, given wrong account`()
    {
      // when
      val webpage = createWebpageModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.addWebpage(webpage)
      }
    }

    @Test
    fun `cannot add webpage, given no account`()
    {
      // when
      val webpage = createWebpageModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.addWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage, when path already exists`()
    {
      // when
      val webpage = createWebpageModel()
      webpageService.addWebpage(webpage)

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        webpageService.addWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage, when invalid path`()
    {
      // when
      val webpageEmail = createWebpageModel()
      webpageEmail.path = "email@barrierelos.ch"

      val webpageNoToplevel = createWebpageModel()
      webpageNoToplevel.path = "barrierelos"

      val webpageSpecialCharacter = createWebpageModel()
      webpageSpecialCharacter.path = "barrierelos?ch"

      // then
      Assertions.assertThrows(InvalidPathException::class.java) {
        webpageService.addWebpage(webpageEmail)
      }

      Assertions.assertThrows(InvalidPathException::class.java) {
        webpageService.addWebpage(webpageNoToplevel)
      }

      Assertions.assertThrows(InvalidPathException::class.java) {
        webpageService.addWebpage(webpageSpecialCharacter)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add webpage, when invalid url`()
    {
      // when
      val webpage = createWebpageModel()
      webpage.path = "/barrierelos"
      webpage.url = "https://barrierelos.ch/test"

      // then
      Assertions.assertThrows(InvalidUrlException::class.java) {
        webpageService.addWebpage(webpage)
      }
    }
  }

  @Nested
  @DisplayName("Update Webpage")
  inner class UpdateWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates webpage, given admin account`()
    {
      // when
      val expected = webpageRepository.save(createWebpageEntity()).toModel()
      expected.path = "/test"
      expected.url = "https://barrierelos.ch/test"
      expected.status = StatusEnum.PENDING_RESCAN

      // then
      val actual = webpageService.updateWebpage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.path, actual.path)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates webpage, given moderator account`()
    {
      // when
      val expected = webpageRepository.save(createWebpageEntity()).toModel()
      expected.status = StatusEnum.BLOCKED

      // then
      val actual = webpageService.updateWebpage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.path, actual.path)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates webpage, given correct contributor account`()
    {
      // when
      val expected = webpageRepository.save(createWebpageEntity(contributor.id)).toModel()
      expected.deleted = true

      // then
      val actual = webpageService.updateWebpage(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.path, actual.path)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage status, given wrong contributor account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity(moderator.id)).toModel()
      webpage.deleted = true

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage, given viewer account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }

    @Test
    fun `cannot update webpage, given no account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage, when illegally modified, given moderator account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity().also {
        it.status = StatusEnum.READY
      }).toModel()
      webpage.path = "/newpath"

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot change status of webpage, when current status is pending, given moderator account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity().also {
        it.status = StatusEnum.PENDING_INITIAL
      }).toModel()
      webpage.status = StatusEnum.READY

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update webpage, when webpage not exists`()
    {
      // when
      val webpage = createWebpageModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageService.updateWebpage(webpage)
      }
    }
  }

  @Nested
  @DisplayName("Get Webpages")
  inner class GetWebpagesTests
  {
    @Test
    fun `gets webpages`()
    {
      // given
      webpageRepository.save(createWebpageEntity().apply {
        path = "/one"
        url = "https://barrierelos.ch/one"
      })
      webpageRepository.save(createWebpageEntity().apply {
        path = "/two"
        url = "https://barrierelos.ch/two"
      })

      // when
      val webpages = webpageService.getWebpages().content

      // then
      webpages.shouldNotBeEmpty()
      webpages.shouldHaveSize(2)
      Assertions.assertTrue(webpages.any { it.path == "/one" })
      Assertions.assertTrue(webpages.any { it.path == "/two" })
    }

    @Test
    fun `gets webpages with headers, when no parameters`()
    {
      // given
      webpageRepository.save(createWebpageEntity().apply {
        path = "/one"
        url = "https://barrierelos.ch/one"
      })
      webpageRepository.save(createWebpageEntity().apply {
        path = "/two"
        url = "https://barrierelos.ch/two"
      })

      // when
      val webpages = webpageService.getWebpages()

      // then
      webpages.page.shouldBe(0)
      webpages.size.shouldBe(2)
      webpages.totalElements.shouldBe(2)
      webpages.totalPages.shouldBe(1)
      webpages.count.shouldBe(2)
      webpages.lastModified.shouldBeGreaterThan(0)
      webpages.content.shouldHaveSize(2)
    }

    @Test
    fun `gets webpages with headers, when with parameters`()
    {
      // given
      webpageRepository.save(createWebpageEntity().apply {
        path = "/one"
        url = "https://barrierelos.ch/one"
      })
      webpageRepository.save(createWebpageEntity().apply {
        path = "/two"
        url = "https://barrierelos.ch/two"
      })

      // when
      val webpages = webpageService.getWebpages(defaultParameters = DefaultParameters(
        page = 1,
        size = 1,
        sort = "path",
        order = OrderEnum.ASC
      )
      )

      // then
      webpages.page.shouldBe(1)
      webpages.size.shouldBe(1)
      webpages.totalElements.shouldBe(2)
      webpages.totalPages.shouldBe(2)
      webpages.count.shouldBe(2)
      webpages.lastModified.shouldBeGreaterThan(0)
      webpages.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Webpage")
  inner class GetWebpageTests
  {
    @Test
    fun `gets webpage`()
    {
      // when
      val expected = webpageRepository.save(createWebpageEntity()).toModel()

      // then
      val actual = webpageService.getWebpage(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get webpage, when webpage not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageService.getWebpage(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Webpage")
  inner class DeleteWebpageTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes webpage, given admin account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity()).toModel()
      val webpagesBefore = webpageService.getWebpages().content

      // then
      Assertions.assertDoesNotThrow {
        webpageService.deleteWebpage(webpage.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageService.getWebpage(webpage.id)
      }

      val webpagesAfter = webpageService.getWebpages().content

      webpagesBefore.shouldContain(webpage)
      webpagesAfter.shouldNotContain(webpage)
      webpagesBefore.shouldContainAll(webpagesAfter)
      webpagesAfter.shouldNotContainAll(webpagesBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes webpage, given moderator account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity()).toModel()
      val webpagesBefore = webpageService.getWebpages().content

      // then
      Assertions.assertDoesNotThrow {
        webpageService.deleteWebpage(webpage.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageService.getWebpage(webpage.id)
      }

      val webpagesAfter = webpageService.getWebpages().content

      webpagesBefore.shouldContain(webpage)
      webpagesAfter.shouldNotContain(webpage)
      webpagesBefore.shouldContainAll(webpagesAfter)
      webpagesAfter.shouldNotContainAll(webpagesBefore)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage, given contributor account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity().apply {
        userFk = contributor.id
      }).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.deleteWebpage(webpage.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage, given viewer account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity().apply {
        userFk = viewer.id
      }).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.deleteWebpage(webpage.id)
      }
    }

    @Test
    fun `cannot delete webpage, given no account`()
    {
      // when
      val webpage = webpageRepository.save(createWebpageEntity()).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        webpageService.deleteWebpage(webpage.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete webpage, when webpage not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        webpageService.deleteWebpage(5000000)
      }
    }
  }
}
