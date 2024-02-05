package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebpageStatisticEntity
import ch.barrierelos.backend.helper.createWebpageStatisticModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageStatisticConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebpageStatisticEntity()

    // then
    val model = createWebpageStatisticModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebpageStatisticModel()

    // then
    val expected = createWebpageStatisticEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.webpageStatisticId, actual.webpageStatisticId)
    Assertions.assertEquals(expected.score, actual.score)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
