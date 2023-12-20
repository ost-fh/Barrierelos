package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.TagEntity
import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.InvalidDomainException
import ch.barrierelos.backend.exceptions.InvalidUrlException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.helper.createWebsiteEntity
import ch.barrierelos.backend.helper.createWebsiteModel
import ch.barrierelos.backend.helper.createWebsiteTagEntity
import ch.barrierelos.backend.helper.createWebsiteTagModel
import ch.barrierelos.backend.model.Tag
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.WebsiteService
import io.kotest.matchers.collections.*
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class WebsiteServiceTests : ServiceTests()
{
  @Autowired
  lateinit var websiteService: WebsiteService

  lateinit var tag: Tag

  @BeforeEach
  override fun beforeEach()
  {
    super.beforeEach()

    tag = tagRepository.save(TagEntity(name = "test")).toModel()
  }

  fun createAndAddWebsite(userId: Long, domain: String, tag: Tag? = null): Website
  {
    return websiteRepository.save(createWebsiteEntity(userId).also {
      it.domain = domain
      it.url = domain
      it.tags.clear()
    }).also { website ->
      if(tag != null)
      {
        website.tags.add(createWebsiteTagEntity(admin.id, website.websiteId).also { it.tag = tag.toEntity() })
        websiteRepository.save(website)
      }
    }.toModel()
  }

  @Nested
  @DisplayName("Add Website")
  inner class AddWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds website, given admin account`()
    {
      // when
      val expected = createWebsiteModel(1, 0)
      expected.tags.first().tag = tag
      val expectedTag = expected.tags.first()

      // then
      val actual = websiteService.addWebsite(expected.copy())
      val actualTag = actual.tags.first()

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.domain, actual.domain)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.category, actual.category)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.tags.size, actual.tags.size)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)

      actual.tags.shouldHaveSize(1)
      Assertions.assertNotEquals(0, actualTag.id)
      Assertions.assertNotEquals(expectedTag.id, actualTag.id)
      Assertions.assertEquals(actual.id, actualTag.websiteId)
      Assertions.assertEquals(expectedTag.userId, actualTag.userId)
      Assertions.assertEquals(expectedTag.tag.id, actualTag.tag.id)
      Assertions.assertEquals(expectedTag.tag.name, actualTag.tag.name)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertEquals(expectedTag.modified, actualTag.modified)
      Assertions.assertEquals(expectedTag.created, actualTag.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website, given wrong account`()
    {
      // when
      val website = createWebsiteModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.addWebsite(website)
      }
    }

    @Test
    fun `cannot add website, given no account`()
    {
      // when
      val website = createWebsiteModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.addWebsite(website)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website, when domain already exists`()
    {
      // when
      val website = createWebsiteModel()
      website.tags.clear()
      websiteService.addWebsite(website)

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        websiteService.addWebsite(website)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website, when duplicate tags`()
    {
      // when
      val website = createWebsiteModel()
      website.tags.clear()
      website.tags.add(createWebsiteTagModel().apply { this.modified = 1 })
      website.tags.add(createWebsiteTagModel().apply { this.modified = 2 })

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        websiteService.addWebsite(website)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website, when invalid domain`()
    {
      // when
      val websiteEmail = createWebsiteModel()
      websiteEmail.tags.clear()
      websiteEmail.domain = "email@barrierelos.ch"

      val websiteNoToplevel = createWebsiteModel()
      websiteNoToplevel.tags.clear()
      websiteNoToplevel.domain = "barrierelos"

      val websiteSpecialCharacter = createWebsiteModel()
      websiteSpecialCharacter.tags.clear()
      websiteSpecialCharacter.domain = "barrierelos?ch"

      // then
      Assertions.assertThrows(InvalidDomainException::class.java) {
        websiteService.addWebsite(websiteEmail)
      }

      Assertions.assertThrows(InvalidDomainException::class.java) {
        websiteService.addWebsite(websiteNoToplevel)
      }

      Assertions.assertThrows(InvalidDomainException::class.java) {
        websiteService.addWebsite(websiteSpecialCharacter)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add website, when invalid url`()
    {
      // when
      val website = createWebsiteModel()
      website.tags.clear()
      website.domain = "barrierelos.ch"
      website.url = "barrierefrei.ch/test"

      // then
      Assertions.assertThrows(InvalidUrlException::class.java) {
        websiteService.addWebsite(website)
      }
    }
  }

  @Nested
  @DisplayName("Update Website")
  inner class UpdateWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates website, given admin account`()
    {
      // when
      val expected = websiteRepository.save(createWebsiteEntity().also { it.tags.clear() }).toModel()
      expected.domain = "admin2.ch"
      expected.url = "https://admin2.ch"
      expected.category = CategoryEnum.GOVERNMENT_CANTONAL
      expected.status = StatusEnum.PENDING_RESCAN
      expected.tags = mutableSetOf()

      // then
      val actual = websiteService.updateWebsite(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.domain, actual.domain)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.category, actual.category)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.tags, actual.tags)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates website, given moderator account`()
    {
      // when
      val expected = websiteRepository.save(createWebsiteEntity().also { it.tags.clear() }).toModel()
      expected.status = StatusEnum.BLOCKED

      // then
      val actual = websiteService.updateWebsite(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.domain, actual.domain)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.category, actual.category)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.tags, actual.tags)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates website, given correct contributor account`()
    {
      // when
      val expected = websiteRepository.save(createWebsiteEntity(contributor.id).also { it.tags.clear() }).toModel()
      expected.deleted = true

      // then
      val actual = websiteService.updateWebsite(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.domain, actual.domain)
      Assertions.assertEquals(expected.url, actual.url)
      Assertions.assertEquals(expected.category, actual.category)
      Assertions.assertEquals(expected.status, actual.status)
      Assertions.assertEquals(expected.tags, actual.tags)
      Assertions.assertEquals(expected.deleted, actual.deleted)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website, given viewer account`()
    {
      // when
      val website = websiteRepository.save(createWebsiteEntity().also { it.tags.clear() }).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    fun `cannot update website, given no account`()
    {
      // when
      val website = websiteRepository.save(createWebsiteEntity().also { it.tags.clear() }).toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website, when illegally modified, given moderator account`()
    {
      // when
      val website = websiteRepository.save(createWebsiteEntity().also {
        it.tags.clear()
        it.status = StatusEnum.READY
      }).toModel()
      website.domain = "newdomain.org"

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot change status of website, when current status is pending, given moderator account`()
    {
      // when
      val website = websiteRepository.save(createWebsiteEntity().also {
        it.tags.clear()
        it.status = StatusEnum.PENDING_INITIAL
      }).toModel()
      website.status = StatusEnum.READY

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website, when tags illegally modified, given moderator account`()
    {
      // when
      val website = createAndAddWebsite(moderator.id, "barrierelos.ch", tag)
      website.tags.first().modified = 1000000

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website, when status changed, given contributor account`()
    {
      // when
      val website = websiteRepository.save(createWebsiteEntity().also {
        it.tags.clear()
        it.status = StatusEnum.BLOCKED
      }).toModel()
      website.status = StatusEnum.READY

      // then
      Assertions.assertThrows(IllegalArgumentException::class.java) {
        websiteService.updateWebsite(website)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update website, when website not exists`()
    {
      // when
      val website = createWebsiteModel()

      // then
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteService.updateWebsite(website)
      }
    }
  }

  @Nested
  @DisplayName("Get Websites")
  inner class GetWebsitesTests
  {
    @Test
    fun `gets websites`()
    {
      // given
      createAndAddWebsite(admin.id, "barrierelos.ch", tag)
      createAndAddWebsite(moderator.id, "barrierelos.org")

      // when
      val websites = websiteService.getWebsites().content

      // then
      websites.shouldNotBeEmpty()
      websites.shouldHaveSize(2)
      Assertions.assertTrue(websites.any { it.domain == "barrierelos.ch" })
      Assertions.assertTrue(websites.any { it.domain == "barrierelos.org" })
      Assertions.assertTrue(websites.any { website -> website.tags.any { websiteTag -> websiteTag.tag.id == tag.id } })
      Assertions.assertTrue(websites.any { it.tags.isEmpty() })
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets websites with headers, when no parameters`()
    {
      // given
      websiteRepository.save(createWebsiteEntity().also {
        it.domain = "barrierelos.ch"
        it.url = "barrierelos.ch"
        it.tags.clear()
      })
      websiteRepository.save(createWebsiteEntity().also {
        it.domain = "barrierelos.org"
        it.url = "barrierelos.org"
        it.tags.clear()
      })

      // when
      val websites = websiteService.getWebsites()

      // then
      websites.page.shouldBe(0)
      websites.size.shouldBe(2)
      websites.totalElements.shouldBe(2)
      websites.totalPages.shouldBe(1)
      websites.count.shouldBe(2)
      websites.lastModified.shouldBeGreaterThan(0)
      websites.content.shouldHaveSize(2)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets websites with headers, when with parameters`()
    {
      // given
      websiteRepository.save(createWebsiteEntity().also {
        it.domain = "barrierelos.ch"
        it.url = "barrierelos.ch"
        it.tags.clear()
      })
      websiteRepository.save(createWebsiteEntity().also {
        it.domain = "barrierelos.org"
        it.url = "barrierelos.org"
        it.tags.clear()
      })

      // when
      val websites = websiteService.getWebsites(defaultParameters = DefaultParameters(
        page = 1,
        size = 1,
        sort = "domain",
        order = OrderEnum.ASC
      )
      )

      // then
      websites.page.shouldBe(1)
      websites.size.shouldBe(1)
      websites.totalElements.shouldBe(2)
      websites.totalPages.shouldBe(2)
      websites.count.shouldBe(2)
      websites.lastModified.shouldBeGreaterThan(0)
      websites.content.shouldHaveSize(1)
    }
  }

  @Nested
  @DisplayName("Get Website")
  inner class GetWebsiteTests
  {
    @Test
    fun `gets website`()
    {
      // when
      val expected = websiteRepository.save(createWebsiteEntity().also {
        it.domain = "barrierelos.ch"
        it.url = "barrierelos.ch"
        it.tags.clear()
      }).toModel()

      // then
      val actual = websiteService.getWebsite(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `cannot get website, when website not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteService.getWebsite(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Website")
  inner class DeleteWebsiteTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes website, given admin account`()
    {
      // when
      val website = createAndAddWebsite(admin.id, "barrierelos.ch", tag)
      val websitesBefore = websiteService.getWebsites().content

      // then
      Assertions.assertDoesNotThrow {
        websiteService.deleteWebsite(website.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteService.getWebsite(website.id)
      }

      websiteTagRepository.findById(website.tags.first().id).shouldBeEmpty()

      val websitesAfter = websiteService.getWebsites().content

      websitesBefore.shouldContain(website)
      websitesAfter.shouldNotContain(website)
      websitesBefore.shouldContainAll(websitesAfter)
      websitesAfter.shouldNotContainAll(websitesBefore)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes website, given moderator account`()
    {
      // when
      val website = createAndAddWebsite(admin.id, "barrierelos.ch", tag)
      val websitesBefore = websiteService.getWebsites().content

      // then
      Assertions.assertDoesNotThrow {
        websiteService.deleteWebsite(website.id)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteService.getWebsite(website.id)
      }

      websiteTagRepository.findById(website.tags.first().id).shouldBeEmpty()

      val websitesAfter = websiteService.getWebsites().content

      websitesBefore.shouldContain(website)
      websitesAfter.shouldNotContain(website)
      websitesBefore.shouldContainAll(websitesAfter)
      websitesAfter.shouldNotContainAll(websitesBefore)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website, given contributor account`()
    {
      // when
      val website = createAndAddWebsite(contributor.id, "barrierelos.ch", tag)

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.deleteWebsite(website.id)
      }
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website, given viewer account`()
    {
      // when
      val website = createAndAddWebsite(viewer.id, "barrierelos.ch", tag)

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.deleteWebsite(website.id)
      }
    }

    @Test
    fun `cannot delete website, given no account`()
    {
      // when
      val website = createAndAddWebsite(contributor.id, "barrierelos.ch", tag)

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        websiteService.deleteWebsite(website.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete website, when website not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        websiteService.deleteWebsite(5000000)
      }
    }
  }
}
