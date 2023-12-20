package ch.barrierelos.backend.integration

import ch.barrierelos.backend.enums.ReasonEnum
import ch.barrierelos.backend.helper.*
import ch.barrierelos.backend.repository.*
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByReportFk
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.ReportRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.UserReportRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.WebsiteReportRepository.Companion.findAllByWebsiteFk
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoryTests
{
  @Autowired
  lateinit var entityManager: TestEntityManager

  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var credentialRepository: CredentialRepository

  @Autowired
  lateinit var tagRepository: TagRepository

  @Autowired
  lateinit var websiteTagRepository: WebsiteTagRepository

  @Autowired
  lateinit var websiteRepository: WebsiteRepository

  @Autowired
  lateinit var webpageRepository: WebpageRepository
  @Autowired
  lateinit var websiteScanRepository: WebsiteScanRepository
  @Autowired
  lateinit var webpageScanRepository: WebpageScanRepository
  @Autowired
  lateinit var reportRepository: ReportRepository
  @Autowired
  lateinit var reportMessageRepository: ReportMessageRepository
  @Autowired
  lateinit var userReportRepository: UserReportRepository
  @Autowired
  lateinit var websiteReportRepository: WebsiteReportRepository

  @Nested
  inner class UserRepositoryTests
  {
    @Test
    fun `finds user by username, when user exists`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByUsername(expected.username)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by username, when not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = userRepository.findByUsername(expected.username)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by username, when wrong username`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByUsername("nickname")

      assertNull(actual)
    }

    @Test
    fun `finds user by id, when user exists`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findById(expected.userId).orElse(null)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by id, when user not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = userRepository.findById(expected.userId).orElse(null)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by id, when wrong id`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findById(5000).orElse(null)

      assertNull(actual)
    }

    @Test
    fun `finds user by issuer and subject, when user exists`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by issuer and subject, when user not exists`()
    {
      // when
      val credential = createCredentialEntity()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong issuer`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject("something", credential.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong subject`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, "something")

      assertNull(actual)
    }
  }

  @Nested
  inner class CredentialRepositoryTests
  {
    @Test
    fun `finds credential by userId, when user exists`()
    {
      // when
      val expected = createCredentialEntity()
      val user = createUserEntity()

      expected.userFk = entityManager.persist(user).userId
      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = credentialRepository.findByUserFk(expected.userFk)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.credentialId)
        assertEquals(expected.credentialId, actual.credentialId)
        assertEquals(expected.userFk, actual.userFk)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find credential by userId, when not exists`()
    {
      // when
      val expected = createCredentialEntity()

      // then
      val actual = credentialRepository.findByUserFk(expected.userFk)

      assertNull(actual)
    }

    @Test
    fun `finds credential by id, when credential exists`()
    {
      // when
      val expected = createCredentialEntity()
      val user = createUserEntity()

      expected.userFk = entityManager.persist(user).userId
      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = credentialRepository.findById(expected.credentialId).orElse(null)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.credentialId)
        assertEquals(expected.credentialId, actual.credentialId)
        assertEquals(expected.userFk, actual.userFk)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find credential by id, when credential not exists`()
    {
      // when
      val expected = createCredentialEntity()

      // then
      val actual = credentialRepository.findById(expected.credentialId).orElse(null)

      assertNull(actual)
    }

    @Test
    fun `cannot find credential by id, when wrong id`()
    {
      // when
      val expected = createCredentialEntity()
      val user = createUserEntity()

      expected.userFk = entityManager.persist(user).userId
      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = credentialRepository.findById(5000).orElse(null)

      assertNull(actual)
    }
  }

  @Nested
  inner class TagRepositoryTests
  {
    @Test
    fun `finds tags, when tags exist`()
    {
      // when
      entityManager.persist(createTagEntity().apply { name = "one" })
      entityManager.persist(createTagEntity().apply { name = "two" })
      entityManager.persist(createTagEntity().apply { name = "three" })
      entityManager.persist(createTagEntity().apply { name = "four" })
      entityManager.flush()

      // then
      val actual = tagRepository.findAll()

      assertNotNull(actual)
      actual.shouldNotBeEmpty()
      actual.shouldHaveSize(4)
    }

    @Test
    fun `cannot find tags, when no tags exist`()
    {
      // then
      val actual = tagRepository.findAll()

      assertNotNull(actual)
      actual.shouldBeEmpty()
      actual.shouldHaveSize(0)
    }

    @Test
    fun `find tag by name, when tag exists`()
    {
      // when
      val expected = createTagEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = tagRepository.findByName(expected.name)

      if(actual != null)
      {
        assertNotEquals(0, actual.tagId)
        assertEquals(expected.tagId, actual.tagId)
        assertEquals(expected.name, actual.name)
      }
    }

    @Test
    fun `cannot find tag by name, when tag not exists`()
    {
      // when
      val expected = createTagEntity()

      // then
      val actual = tagRepository.findByName(expected.name)

      assertNull(actual)
    }
  }

  @Nested
  inner class WebsiteTagRepositoryTests
  {
    @Test
    fun `finds websiteTag by id, when websiteTag exists`()
    {
      // given
      val user = entityManager.persist(createUserEntity())

      val website = createWebsiteEntity()
      website.user = user
      website.tags.clear()

      entityManager.persist(website)

      // when
      val expected = createWebsiteTagEntity()
      expected.userFk = user.userId
      expected.websiteFk = website.websiteId

      entityManager.persist(expected.tag)
      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = websiteTagRepository.findById(expected.websiteTagId).orElse(null)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.websiteTagId)
        assertEquals(expected.websiteTagId, actual.websiteTagId)
        assertEquals(expected.userFk, actual.userFk)
        assertEquals(expected.tag.tagId, actual.tag.tagId)
        assertEquals(expected.tag.name, actual.tag.name)
        assertEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }
    }

    @Test
    fun `cannot find websiteTag by id, when websiteTag not exists`()
    {
      // when
      val expected = createWebsiteTagEntity()

      // then
      val actual = websiteTagRepository.findById(expected.websiteTagId).orElse(null)

      assertNull(actual)
    }
  }

  @Nested
  inner class WebsiteRepositoryTests
  {
    @Test
    fun `finds website by id, when website exists`()
    {
      // given
      val user = entityManager.persist(createUserEntity())

      // when
      val expected = createWebsiteEntity()
      expected.user = user
      expected.tags.clear()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = websiteRepository.findById(expected.websiteId).orElse(null)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.websiteId)
        assertEquals(expected.websiteId, actual.websiteId)
        assertEquals(expected.user, actual.user)
        assertEquals(expected.domain, actual.domain)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.tags.size, actual.tags.size)
        assertEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }
    }

    @Test
    fun `cannot find website by id, when website not exists`()
    {
      // when
      val expected = createWebsiteEntity()

      // then
      val actual = websiteRepository.findById(expected.websiteId).orElse(null)

      assertNull(actual)
    }

    @Test
    fun `finds website by domain, when website exists`()
    {
      // given
      val user = entityManager.persist(createUserEntity())

      // when
      val expected = createWebsiteEntity()
      expected.user = user
      expected.tags.clear()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = websiteRepository.findByDomainContaining(expected.domain)

      assertNotNull(actual)
      actual.shouldNotBeEmpty()
      actual.shouldHaveSize(1)
      actual.first()

      assertNotEquals(0, actual.first().websiteId)
      assertEquals(expected.websiteId, actual.first().websiteId)
      assertEquals(expected.user, actual.first().user)
      assertEquals(expected.domain, actual.first().domain)
      assertEquals(expected.url, actual.first().url)
      assertEquals(expected.category, actual.first().category)
      assertEquals(expected.status, actual.first().status)
      assertEquals(expected.tags.size, actual.first().tags.size)
      assertEquals(expected.modified, actual.first().modified)
      assertEquals(expected.created, actual.first().created)
    }

    @Test
    fun `cannot find website by domain, when website not exists`()
    {
      // when
      val expected = createWebsiteEntity()

      // then
      val actual = websiteRepository.findByDomainContaining(expected.domain)

      assertNotNull(actual)
      actual.shouldBeEmpty()
      actual.shouldHaveSize(0)
    }
  }

  @Nested
  inner class WebpageRepositoryTests
  {
    @Test
    fun `exists by displayUrl, when displayUrl exists`()
    {
      // when
      val user = createUserEntity()
      val website = createWebsiteEntity(user)
      website.tags.clear()
      val webpage = createWebpageEntity(user)
      webpage.website = website

      entityManager.persist(user)
      entityManager.persist(website)
      entityManager.persist(webpage)
      entityManager.flush()

      // then
      val exists = webpageRepository.existsByDisplayUrl(webpage.displayUrl)

      assertTrue(exists)
    }

    @Test
    fun `exists by displayUrl, when displayUrl not exists`()
    {
      // when
      val user = createUserEntity()
      val website = createWebsiteEntity(user)
      website.tags.clear()
      val webpage = createWebpageEntity(user)
      webpage.website = website
      val expected = webpage.displayUrl
      webpage.displayUrl = "test.$expected"

      entityManager.persist(user)
      entityManager.persist(website)
      entityManager.persist(webpage)
      entityManager.flush()

      // then
      val exists = webpageRepository.existsByDisplayUrl(expected)

      assertFalse(exists)
    }
  }

  @Nested
  inner class WebsiteScanRepositoryTests
  {
    @Test
    fun `finds by website fk, when website fk exists`()
    {
      // given
      val websiteFk = 25L

      // when
      entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk, websiteStatisticFk = 1))
      entityManager.persist(createWebsiteScanEntity(websiteFk = websiteFk, websiteStatisticFk = 2))
      entityManager.flush()

      // then
      val actual = websiteScanRepository.findAllByWebsiteFk(websiteFk)

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.websiteStatisticFk == 1L }
      actual.shouldHaveSingleElement { it.websiteStatisticFk == 2L }
    }

    @Test
    fun `not exists by website fk, when website fk not exists`()
    {
      // when
      entityManager.persist(createWebsiteScanEntity(websiteFk = 25))
      entityManager.flush()

      // then
      val websiteScans = websiteScanRepository.findAllByWebsiteFk(5000)

      websiteScans.shouldBeEmpty()
    }
  }

  @Nested
  inner class WebpageScanRepositoryTests
  {
    @Test
    fun `finds by webpage fk, when webpage fk exists`()
    {
      // given
      val webpageFk = 25L

      // when
      entityManager.persist(createWebpageScanEntity(webpageFk = webpageFk, webpageStatisticFk = 1))
      entityManager.persist(createWebpageScanEntity(webpageFk = webpageFk, webpageStatisticFk = 2))
      entityManager.flush()

      // then
      val actual = webpageScanRepository.findAllByWebpageFk(webpageFk)

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.webpageStatisticFk == 1L }
      actual.shouldHaveSingleElement { it.webpageStatisticFk == 2L }
    }

    @Test
    fun `not exists by webpage fk, when webpage fk not exists`()
    {
      // when
      entityManager.persist(createWebpageScanEntity(webpageFk = 25))
      entityManager.flush()

      // then
      val webpageScans = webpageScanRepository.findAllByWebpageFk(5000)

      webpageScans.shouldBeEmpty()
    }
  }

  @Nested
  inner class ReportRepositoryTests
  {
    @Test
    fun `finds by user fk, when user fk exists`()
    {
      // given
      val userFk = 25L

      // when
      entityManager.persist(createReportEntity(userFk = userFk).apply { reason = ReasonEnum.INCORRECT })
      entityManager.persist(createReportEntity(userFk = userFk).apply { reason = ReasonEnum.MISLEADING })
      entityManager.flush()

      // then
      val actual = reportRepository.findAllByUserFk(userFk).content

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.reason == ReasonEnum.INCORRECT }
      actual.shouldHaveSingleElement { it.reason == ReasonEnum.MISLEADING }
    }

    @Test
    fun `not exists by user fk, when user fk not exists`()
    {
      // when
      entityManager.persist(createReportEntity(userFk = 25))
      entityManager.flush()

      // then
      val reports = reportRepository.findAllByUserFk(5000).content

      reports.shouldBeEmpty()
    }
  }

  @Nested
  inner class ReportMessageRepositoryTests
  {
    @Test
    fun `finds by report fk, when report fk exists`()
    {
      // given
      val reportFk = 25L

      // when
      entityManager.persist(createReportMessageEntity(reportFk = reportFk).apply { message = "one" })
      entityManager.persist(createReportMessageEntity(reportFk = reportFk).apply { message = "two" })
      entityManager.flush()

      // then
      val actual = reportMessageRepository.findAllByReportFk(reportFk).content

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.message == "one" }
      actual.shouldHaveSingleElement { it.message == "two" }
    }

    @Test
    fun `not exists by report fk, when report fk not exists`()
    {
      // when
      entityManager.persist(createReportMessageEntity(reportFk = 25))
      entityManager.flush()

      // then
      val reportMessages = reportMessageRepository.findAllByReportFk(5000).content

      reportMessages.shouldBeEmpty()
    }

    @Test
    fun `finds by user fk, when user fk exists`()
    {
      // given
      val userFk = 25L

      // when
      entityManager.persist(createReportMessageEntity(userFk = userFk).apply { message = "one" })
      entityManager.persist(createReportMessageEntity(userFk = userFk).apply { message = "two" })
      entityManager.flush()

      // then
      val actual = reportMessageRepository.findAllByUserFk(userFk).content

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.message == "one" }
      actual.shouldHaveSingleElement { it.message == "two" }
    }

    @Test
    fun `not exists by user fk, when user fk not exists`()
    {
      // when
      entityManager.persist(createReportMessageEntity(userFk = 25))
      entityManager.flush()

      // then
      val reportMessages = reportMessageRepository.findAllByUserFk(5000).content

      reportMessages.shouldBeEmpty()
    }
  }

  @Nested
  inner class UserReportRepositoryTests
  {
    @Test
    fun `finds by user fk, when user fk exists`()
    {
      // given
      val userFk = 25L

      // when
      entityManager.persist(createUserReportEntity(userFk = userFk).apply { report.reason = ReasonEnum.INCORRECT })
      entityManager.persist(createUserReportEntity(userFk = userFk).apply { report.reason = ReasonEnum.MISLEADING })
      entityManager.flush()

      // then
      val actual = userReportRepository.findAllByUserFk(userFk).content

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.INCORRECT }
      actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.MISLEADING }
    }

    @Test
    fun `not exists by user fk, when user fk not exists`()
    {
      // when
      entityManager.persist(createUserReportEntity(userFk = 25))
      entityManager.flush()

      // then
      val userReports = userReportRepository.findAllByUserFk(5000).content

      userReports.shouldBeEmpty()
    }
  }

  @Nested
  inner class WebsiteReportRepositoryTests
  {
    @Test
    fun `finds by website fk, when website fk exists`()
    {
      // given
      val websiteFk = 25L

      // when
      entityManager.persist(createWebsiteReportEntity(websiteFk = websiteFk).apply { report.reason = ReasonEnum.INCORRECT })
      entityManager.persist(createWebsiteReportEntity(websiteFk = websiteFk).apply { report.reason = ReasonEnum.MISLEADING })
      entityManager.flush()

      // then
      val actual = websiteReportRepository.findAllByWebsiteFk(websiteFk).content

      actual.shouldHaveSize(2)
      actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.INCORRECT }
      actual.shouldHaveSingleElement { it.report.reason == ReasonEnum.MISLEADING }
    }

    @Test
    fun `not exists by website fk, when website fk not exists`()
    {
      // when
      entityManager.persist(createWebsiteReportEntity(websiteFk = 25))
      entityManager.flush()

      // then
      val websiteReports = websiteReportRepository.findAllByWebsiteFk(5000).content

      websiteReports.shouldBeEmpty()
    }
  }
}
