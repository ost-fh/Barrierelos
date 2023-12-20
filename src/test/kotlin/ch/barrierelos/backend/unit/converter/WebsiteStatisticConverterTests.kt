package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebsiteStatisticEntity
import ch.barrierelos.backend.helper.createWebsiteStatisticModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteStatisticConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteStatisticEntity()

    // then
    val model = createWebsiteStatisticModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteStatisticModel()

    // then
    val expected = createWebsiteStatisticEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteStatisticId, actual.websiteStatisticId)
    Assertions.assertEquals(expected.score, actual.score)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
