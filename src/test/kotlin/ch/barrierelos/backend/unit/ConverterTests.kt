package ch.barrierelos.backend.unit

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.*
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
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

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createTagEntity().apply { name = "one" },
        createTagEntity().apply { name = "two" }
      )

      // then
      val models = setOf(
        createTagModel().apply { name = "one" },
        createTagModel().apply { name = "two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createTagModel().apply { name = "one" },
        createTagModel().apply { name = "two" }
      )

      // then
      val expected = setOf(
        createTagEntity().apply { name = "one" },
        createTagEntity().apply { name = "two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.name == "one" }
      actual.shouldHaveSingleElement { it.name == "two" }
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
      assertEquals(expected.websiteFk, actual.websiteFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.tag.tagId, actual.tag.tagId)
      assertEquals(expected.tag.name, actual.tag.name)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebsiteTagEntity().apply { tag.name = "one" },
        createWebsiteTagEntity().apply { tag.name = "two" }
      )

      // then
      val models = setOf(
        createWebsiteTagModel().apply { tag.name = "one" },
        createWebsiteTagModel().apply { tag.name = "two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebsiteTagModel().apply { tag.name = "one" },
        createWebsiteTagModel().apply { tag.name = "two" }
      )

      // then
      val expected = setOf(
        createWebsiteTagEntity().apply { tag.name = "one" },
        createWebsiteTagEntity().apply { tag.name = "two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.tag.name == "one" }
      actual.shouldHaveSingleElement { it.tag.name == "two" }
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
      assertEquals(expected.deleted, actual.deleted)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebsiteEntity().apply { domain = "one" },
        createWebsiteEntity().apply { domain = "two" }
      )

      // then
      val models = setOf(
        createWebsiteModel().apply { domain = "one" },
        createWebsiteModel().apply { domain = "two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebsiteModel().apply { domain = "one" },
        createWebsiteModel().apply { domain = "two" }
      )

      // then
      val expected = setOf(
        createWebsiteEntity().apply { domain = "one" },
        createWebsiteEntity().apply { domain = "two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.domain == "one" }
      actual.shouldHaveSingleElement { it.domain == "two" }
    }
  }

  @Nested
  inner class WebpageTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebpageEntity()

      // then
      val model = createWebpageModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebpageModel()

      // then
      val expected = createWebpageEntity()

      val actual = model.toEntity()

      assertEquals(expected.webpageId, actual.webpageId)
      assertEquals(expected.websiteFk, actual.websiteFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.path, actual.path)
      assertEquals(expected.url, actual.url)
      assertEquals(expected.status, actual.status)
      assertEquals(expected.deleted, actual.deleted)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebpageEntity().apply { path = "one" },
        createWebpageEntity().apply { path = "two" }
      )

      // then
      val models = setOf(
        createWebpageModel().apply { path = "one" },
        createWebpageModel().apply { path = "two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebpageModel().apply { path = "one" },
        createWebpageModel().apply { path = "two" }
      )

      // then
      val expected = setOf(
        createWebpageEntity().apply { path = "one" },
        createWebpageEntity().apply { path = "two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.path == "one" }
      actual.shouldHaveSingleElement { it.path == "two" }
    }
  }
}
