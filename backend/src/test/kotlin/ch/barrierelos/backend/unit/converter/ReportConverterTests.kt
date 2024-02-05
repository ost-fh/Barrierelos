package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.helper.createReportEntity
import ch.barrierelos.backend.helper.createReportModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createReportEntity()

    // then
    val model = createReportModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createReportModel()

    // then
    val expected = createReportEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.reportId, actual.reportId)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.reason, actual.reason)
    Assertions.assertEquals(expected.state, actual.state)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }

  @Test
  fun `converts to models, when entities`()
  {
    // when
    val entities = setOf(
      createReportEntity().apply { state = StateEnum.OPEN },
      createReportEntity().apply { state = StateEnum.CLOSED }
    )

    // then
    val models = setOf(
      createReportModel().apply { state = StateEnum.OPEN },
      createReportModel().apply { state = StateEnum.CLOSED }
    )

    Assertions.assertEquals(models, entities.toModels())
  }

  @Test
  fun `converts to entities, when models`()
  {
    // when
    val models = setOf(
      createReportModel().apply { state = StateEnum.OPEN },
      createReportModel().apply { state = StateEnum.CLOSED }
    )

    // then
    val expected = setOf(
      createReportEntity().apply { state = StateEnum.OPEN },
      createReportEntity().apply { state = StateEnum.CLOSED }
    )
    val actual = models.toEntities()

    actual.shouldHaveSize(expected.size)
    actual.shouldHaveSingleElement { it.state == StateEnum.OPEN }
    actual.shouldHaveSingleElement { it.state == StateEnum.CLOSED }
  }
}
