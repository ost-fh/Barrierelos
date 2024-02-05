package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebpageEntity
import ch.barrierelos.backend.helper.createWebpageModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebpageEntity()

    // then
    val model = createWebpageModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebpageModel()

    // then
    val expected = createWebpageEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.webpageId, actual.webpageId)
    Assertions.assertEquals(expected.website.websiteId, actual.website.websiteId)
    Assertions.assertEquals(expected.user.userId, actual.user.userId)
    Assertions.assertEquals(expected.displayUrl, actual.displayUrl)
    Assertions.assertEquals(expected.url, actual.url)
    Assertions.assertEquals(expected.status, actual.status)
    Assertions.assertEquals(expected.deleted, actual.deleted)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
