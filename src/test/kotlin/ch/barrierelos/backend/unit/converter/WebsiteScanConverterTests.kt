package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebsiteScanEntity
import ch.barrierelos.backend.helper.createWebsiteScanModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteScanConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteScanEntity()

    // then
    val model = createWebsiteScanModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteScanModel()

    // then
    val expected = createWebsiteScanEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteScanId, actual.websiteScanId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.websiteStatisticFk, actual.websiteStatisticFk)
    Assertions.assertEquals(expected.websiteResultFk, actual.websiteResultFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
