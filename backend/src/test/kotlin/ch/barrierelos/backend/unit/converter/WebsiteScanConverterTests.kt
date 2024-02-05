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
    Assertions.assertEquals(expected.website.websiteId, actual.website.websiteId)
    Assertions.assertEquals(expected.websiteStatistic!!.websiteStatisticId, actual.websiteStatistic!!.websiteStatisticId)
    Assertions.assertEquals(expected.websiteResult, actual.websiteResult)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }

  @Test
  fun `converts to models, when entities`()
  {
    // when
    val entities = setOf(
      createWebsiteScanEntity().apply { website.websiteId = 1L },
      createWebsiteScanEntity().apply { website.websiteId = 2L }
    )

    // then
    val models = setOf(
      createWebsiteScanModel().apply { website.id = 1L },
      createWebsiteScanModel().apply { website.id = 2L }
    )

    Assertions.assertEquals(models, entities.toModels())
  }

  @Test
  fun `converts to entities, when models`()
  {
    // when
    val models = setOf(
      createWebsiteScanModel().apply { website.id = 1L },
      createWebsiteScanModel().apply { website.id = 2L }
    )

    // then
    val expected = setOf(
      createWebsiteScanEntity().apply { website.websiteId = 1L },
      createWebsiteScanEntity().apply { website.websiteId = 2L }
    )
    val actual = models.toEntities()

    actual.shouldHaveSize(expected.size)
    actual.shouldHaveSingleElement { it.website.websiteId == 1L }
    actual.shouldHaveSingleElement { it.website.websiteId == 2L }
  }
}
