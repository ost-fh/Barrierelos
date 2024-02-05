package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebsiteEntity
import ch.barrierelos.backend.helper.createWebsiteModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteEntity()

    // then
    val model = createWebsiteModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteModel()

    // then
    val expected = createWebsiteEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteId, actual.websiteId)
    Assertions.assertEquals(expected.user.userId, actual.user.userId)
    Assertions.assertEquals(expected.domain, actual.domain)
    Assertions.assertEquals(expected.url, actual.url)
    Assertions.assertEquals(expected.category, actual.category)
    Assertions.assertEquals(expected.status, actual.status)
    Assertions.assertEquals(expected.tags.size, actual.tags.size)
    Assertions.assertEquals(expected.deleted, actual.deleted)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
