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
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.tag.tagId, actual.tag.tagId)
      assertEquals(expected.tag.name, actual.tag.name)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }
}
