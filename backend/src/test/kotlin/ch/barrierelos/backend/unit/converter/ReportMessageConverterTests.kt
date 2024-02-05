package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createReportMessageEntity
import ch.barrierelos.backend.helper.createReportMessageModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportMessageConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createReportMessageEntity()

    // then
    val model = createReportMessageModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createReportMessageModel()

    // then
    val expected = createReportMessageEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.reportMessageId, actual.reportMessageId)
    Assertions.assertEquals(expected.reportFk, actual.reportFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.message, actual.message)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
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

    Assertions.assertEquals(models, entities.toModels())
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
