package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebsiteTagEntity
import ch.barrierelos.backend.helper.createWebsiteTagModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteTagConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteTagEntity()

    // then
    val model = createWebsiteTagModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteTagModel()

    // then
    val expected = createWebsiteTagEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteTagId, actual.websiteTagId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.tag.tagId, actual.tag.tagId)
    Assertions.assertEquals(expected.tag.name, actual.tag.name)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
