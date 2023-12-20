package ch.barrierelos.backend.unit

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.enums.StateEnum
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
      assertEquals(expected.website.websiteId, actual.website.websiteId)
      assertEquals(expected.user.userId, actual.user.userId)
      assertEquals(expected.displayUrl, actual.displayUrl)
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
        createWebpageEntity().apply { displayUrl = "admin.ch/vbs/infos/one" },
        createWebpageEntity().apply { displayUrl = "admin.ch/vbs/infos/two" }
      )

      // then
      val models = setOf(
        createWebpageModel().apply { displayUrl = "admin.ch/vbs/infos/one" },
        createWebpageModel().apply { displayUrl = "admin.ch/vbs/infos/two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebpageModel().apply { displayUrl = "admin.ch/vbs/infos/one" },
        createWebpageModel().apply { displayUrl = "admin.ch/vbs/infos/two" }
      )

      // then
      val expected = setOf(
        createWebpageEntity().apply { displayUrl = "admin.ch/vbs/infos/one" },
        createWebpageEntity().apply { displayUrl = "admin.ch/vbs/infos/two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.displayUrl == "admin.ch/vbs/infos/one" }
      actual.shouldHaveSingleElement { it.displayUrl == "admin.ch/vbs/infos/two" }
    }
  }

  @Nested
  inner class WebsiteStatisticTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebsiteStatisticEntity()

      // then
      val model = createWebsiteStatisticModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebsiteStatisticModel()

      // then
      val expected = createWebsiteStatisticEntity()

      val actual = model.toEntity()

      assertEquals(expected.websiteStatisticId, actual.websiteStatisticId)
      assertEquals(expected.score, actual.score)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebsiteStatisticEntity().apply { score = 40.0 },
        createWebsiteStatisticEntity().apply { score = 60.0 }
      )

      // then
      val models = setOf(
        createWebsiteStatisticModel().apply { score = 40.0 },
        createWebsiteStatisticModel().apply { score = 60.0 }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebsiteStatisticModel().apply { score = 40.0 },
        createWebsiteStatisticModel().apply { score = 60.0 }
      )

      // then
      val expected = setOf(
        createWebsiteStatisticEntity().apply { score = 40.0 },
        createWebsiteStatisticEntity().apply { score = 60.0 }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.score == 40.0 }
      actual.shouldHaveSingleElement { it.score == 60.0 }
    }
  }

  @Nested
  inner class WebpageStatisticTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebpageStatisticEntity()

      // then
      val model = createWebpageStatisticModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebpageStatisticModel()

      // then
      val expected = createWebpageStatisticEntity()

      val actual = model.toEntity()

      assertEquals(expected.webpageStatisticId, actual.webpageStatisticId)
      assertEquals(expected.score, actual.score)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebpageStatisticEntity().apply { score = 40.0 },
        createWebpageStatisticEntity().apply { score = 60.0 }
      )

      // then
      val models = setOf(
        createWebpageStatisticModel().apply { score = 40.0 },
        createWebpageStatisticModel().apply { score = 60.0 }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebpageStatisticModel().apply { score = 40.0 },
        createWebpageStatisticModel().apply { score = 60.0 }
      )

      // then
      val expected = setOf(
        createWebpageStatisticEntity().apply { score = 40.0 },
        createWebpageStatisticEntity().apply { score = 60.0 }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.score == 40.0 }
      actual.shouldHaveSingleElement { it.score == 60.0 }
    }
  }

  @Nested
  inner class WebsiteScanTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebsiteScanEntity()

      // then
      val model = createWebsiteScanModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebsiteScanModel()

      // then
      val expected = createWebsiteScanEntity()

      val actual = model.toEntity()

      assertEquals(expected.websiteScanId, actual.websiteScanId)
      assertEquals(expected.websiteFk, actual.websiteFk)
      assertEquals(expected.websiteStatisticFk, actual.websiteStatisticFk)
      assertEquals(expected.websiteResultFk, actual.websiteResultFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebsiteScanEntity().apply { websiteFk = 1L },
        createWebsiteScanEntity().apply { websiteFk = 2L }
      )

      // then
      val models = setOf(
        createWebsiteScanModel().apply { websiteId = 1L },
        createWebsiteScanModel().apply { websiteId = 2L }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebsiteScanModel().apply { websiteId = 1L },
        createWebsiteScanModel().apply { websiteId = 2L }
      )

      // then
      val expected = setOf(
        createWebsiteScanEntity().apply { websiteFk = 1L },
        createWebsiteScanEntity().apply { websiteFk = 2L }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.websiteFk == 1L }
      actual.shouldHaveSingleElement { it.websiteFk == 2L }
    }
  }

  @Nested
  inner class WebpageScanTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createWebpageScanEntity()

      // then
      val model = createWebpageScanModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createWebpageScanModel()

      // then
      val expected = createWebpageScanEntity()

      val actual = model.toEntity()

      assertEquals(expected.webpageScanId, actual.webpageScanId)
      assertEquals(expected.webpageFk, actual.webpageFk)
      assertEquals(expected.webpageStatisticFk, actual.webpageStatisticFk)
      assertEquals(expected.webpageResultFk, actual.webpageResultFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createWebpageScanEntity().apply { webpageFk = 1L },
        createWebpageScanEntity().apply { webpageFk = 2L }
      )

      // then
      val models = setOf(
        createWebpageScanModel().apply { webpageId = 1L },
        createWebpageScanModel().apply { webpageId = 2L }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createWebpageScanModel().apply { webpageId = 1L },
        createWebpageScanModel().apply { webpageId = 2L }
      )

      // then
      val expected = setOf(
        createWebpageScanEntity().apply { webpageFk = 1L },
        createWebpageScanEntity().apply { webpageFk = 2L }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.webpageFk == 1L }
      actual.shouldHaveSingleElement { it.webpageFk == 2L }
    }
  }

  @Nested
  inner class ReportTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createReportEntity()

      // then
      val model = createReportModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createReportModel()

      // then
      val expected = createReportEntity()

      val actual = model.toEntity()

      assertEquals(expected.reportId, actual.reportId)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.reason, actual.reason)
      assertEquals(expected.state, actual.state)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createReportEntity().apply { state = StateEnum.OPEN },
        createReportEntity().apply { state = StateEnum.CLOSED }
      )

      // then
      val models = setOf(
        createReportModel().apply { state = StateEnum.OPEN },
        createReportModel().apply { state = StateEnum.CLOSED }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createReportModel().apply { state = StateEnum.OPEN },
        createReportModel().apply { state = StateEnum.CLOSED }
      )

      // then
      val expected = setOf(
        createReportEntity().apply { state = StateEnum.OPEN },
        createReportEntity().apply { state = StateEnum.CLOSED }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.state == StateEnum.OPEN }
      actual.shouldHaveSingleElement { it.state == StateEnum.CLOSED }
    }
  }

  @Nested
  inner class ReportMessageTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createReportMessageEntity()

      // then
      val model = createReportMessageModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createReportMessageModel()

      // then
      val expected = createReportMessageEntity()

      val actual = model.toEntity()

      assertEquals(expected.reportMessageId, actual.reportMessageId)
      assertEquals(expected.reportFk, actual.reportFk)
      assertEquals(expected.userFk, actual.userFk)
      assertEquals(expected.message, actual.message)
      assertEquals(expected.modified, actual.modified)
      assertEquals(expected.created, actual.created)
    }

    @Test
    fun `converts to models, when entities`()
    {
      // when
      val entities = setOf(
        createReportMessageEntity().apply { message = "one" },
        createReportMessageEntity().apply { message = "two" }
      )

      // then
      val models = setOf(
        createReportMessageModel().apply { message = "one" },
        createReportMessageModel().apply { message = "two" }
      )

      assertEquals(models, entities.toModels())
    }

    @Test
    fun `converts to entities, when models`()
    {
      // when
      val models = setOf(
        createReportMessageModel().apply { message = "one" },
        createReportMessageModel().apply { message = "two" },
      )

      // then
      val expected = setOf(
        createReportMessageEntity().apply { message = "one" },
        createReportMessageEntity().apply { message = "two" }
      )
      val actual = models.toEntities()

      actual.shouldHaveSize(expected.size)
      actual.shouldHaveSingleElement { it.message == "one" }
      actual.shouldHaveSingleElement { it.message == "two" }
    }
  }
}
