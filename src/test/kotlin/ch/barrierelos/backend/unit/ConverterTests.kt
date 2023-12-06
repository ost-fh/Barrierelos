package ch.barrierelos.backend.unit

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.helper.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConverterTests
{
  @Nested
  inner class UserTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createUserEntity()

      // then
      val model = createUserModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createUserModel()

      // then
      val expected = createUserEntity()

      val actual = model.toEntity()

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
    fun `converts to model, when entity`()
    {
      // when
      val entity = createCredentialEntity()

      // then
      val model = createCredentialModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createCredentialModel()

      // then
      val expected = createCredentialEntity()

      val actual = model.toEntity()

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
    fun `converts to model, when entity`()
    {
      // when
      val entity = createTagEntity()

      // then
      val model = createTagModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createTagModel()

      // then
      val expected = createTagEntity()

      val actual = model.toEntity()

      assertEquals(expected.tagId, actual.tagId)
      assertEquals(expected.name, actual.name)
    }
  }

  @Nested
  inner class WebsiteTagTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebsiteTagEntity()

      // then
      val model = createWebsiteTagModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebsiteTagModel()

      // then
      val expected = createWebsiteTagEntity()

      val actual = model.toEntity()

      assertEquals(expected.websiteTagId, actual.websiteTagId)
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
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebsiteEntity()

      // then
      val model = createWebsiteModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebsiteModel()

      // then
      val expected = createWebsiteEntity()

      val actual = model.toEntity()

      assertEquals(expected.websiteId, actual.websiteId)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.domain, actual.domain)
      assertEquals(expected.url, actual.url)
      assertEquals(expected.category, actual.category)
      assertEquals(expected.status, actual.status)
      assertEquals(expected.tags.size, actual.tags.size)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }
  }
}
