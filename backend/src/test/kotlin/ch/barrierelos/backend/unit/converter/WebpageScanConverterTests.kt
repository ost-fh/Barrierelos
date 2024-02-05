package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createWebpageScanEntity
import ch.barrierelos.backend.helper.createWebpageScanModel
import ch.barrierelos.backend.helper.createWebsiteScanEntity
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
    val scan = createWebsiteScanEntity()

    // then
    val expected = createWebpageScanEntity()

    val actual = model.toEntity(scan)

    Assertions.assertEquals(expected.webpageScanId, actual.webpageScanId)
    Assertions.assertEquals(expected.webpage.webpageId, actual.webpage.webpageId)
    Assertions.assertEquals(expected.webpageStatistic!!.webpageStatisticId, actual.webpageStatistic!!.webpageStatisticId)
    Assertions.assertEquals(expected.webpageResult, actual.webpageResult)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }

  @Test
  fun `converts to models, when entities`()
  {
    // when
    val entities = setOf(
      createWebpageScanEntity().apply { webpage.webpageId = 1L },
      createWebpageScanEntity().apply { webpage.webpageId = 2L }
    )

    // then
    val models = setOf(
      createWebpageScanModel().apply { webpage.id = 1L },
      createWebpageScanModel().apply { webpage.id = 2L }
    )

    Assertions.assertEquals(models, entities.toModels())
  }

  @Test
  fun `converts to entities, when models`()
  {
    // when
    val models = setOf(
      createWebpageScanModel().apply { webpage.id = 1L },
      createWebpageScanModel().apply { webpage.id = 2L }
    )

    // then
    val expected = setOf(
      createWebpageScanEntity().apply { webpage.webpageId = 1L },
      createWebpageScanEntity().apply { webpage.webpageId = 2L }
    )
    val actual = models.toEntities(createWebsiteScanEntity())

    actual.shouldHaveSize(expected.size)
    actual.shouldHaveSingleElement { it.webpage.webpageId == 1L }
    actual.shouldHaveSingleElement { it.webpage.webpageId == 2L }
  }
}
