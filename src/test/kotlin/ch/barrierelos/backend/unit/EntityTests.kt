package ch.barrierelos.backend.unit

import ch.barrierelos.backend.helper.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EntityTests
{
  @Nested
  inner class UserTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = createUserEntity()

      assertEquals(expected.userId, actual.userId)
      assertEquals(expected.username, actual.username)
      assertEquals(expected.firstname, actual.firstname)
      assertEquals(expected.lastname, actual.lastname)
      assertEquals(expected.email, actual.email)
      assertEquals(expected.roles, actual.roles)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }

  @Nested
  inner class CredentialTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createCredentialEntity()

      // then
      val actual = createCredentialEntity()

      assertEquals(expected.credentialId, actual.credentialId)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.password, actual.password)
      assertEquals(expected.issuer, actual.issuer)
      assertEquals(expected.subject, actual.subject)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }

  @Nested
  inner class TagTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createTagEntity()

      // then
      val actual = createTagEntity()

      assertEquals(expected.tagId, actual.tagId)
      assertEquals(expected.name, actual.name)
    }
  }

  @Nested
  inner class WebsiteTagTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteTagEntity()

      // then
      val actual = createWebsiteTagEntity()

      assertEquals(expected.websiteTagId, actual.websiteTagId)
      assertEquals(expected.websiteFk, actual.websiteFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.tag.tagId, actual.tag.tagId)
      assertEquals(expected.tag.name, actual.tag.name)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }

  @Nested
  inner class WebsiteTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteEntity()

      // then
      val actual = createWebsiteEntity()

      assertEquals(expected.websiteId, actual.websiteId)
      assertEquals(expected.user.userId, actual.user.userId)
      assertEquals(expected.domain, actual.domain)
      assertEquals(expected.url, actual.url)
      assertEquals(expected.category, actual.category)
      assertEquals(expected.status, actual.status)
      assertEquals(expected.tags.size, actual.tags.size)
      assertEquals(expected.deleted, actual.deleted)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }

  @Nested
  inner class WebpageTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebpageEntity()

      // then
      val actual = createWebpageEntity()

      assertEquals(expected.webpageId, actual.webpageId)
      assertEquals(expected.website.websiteId, actual.website.websiteId)
      assertEquals(expected.user.userId, actual.user.userId)
      assertEquals(expected.displayUrl, actual.displayUrl)
      assertEquals(expected.url, actual.url)
      assertEquals(expected.status, actual.status)
      assertEquals(expected.deleted, actual.deleted)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }

  @Nested
  inner class WebsiteStatisticTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteStatisticEntity()

      // then
      val actual = createWebsiteStatisticEntity()

      assertEquals(expected.websiteStatisticId, actual.websiteStatisticId)
      assertEquals(expected.score, actual.score)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }
}
