package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebpageScanEntity
import ch.barrierelos.backend.helper.createWebpageScanModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageScanConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebpageScanEntity()

    // then
    val model = createWebpageScanModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebpageScanModel()

    // then
    val expected = createWebpageScanEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.webpageScanId, actual.webpageScanId)
    Assertions.assertEquals(expected.webpageFk, actual.webpageFk)
    Assertions.assertEquals(expected.webpageStatisticFk, actual.webpageStatisticFk)
    Assertions.assertEquals(expected.webpageResultFk, actual.webpageResultFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
