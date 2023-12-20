package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.enums.StateEnum
import ch.barrierelos.backend.helper.createWebsiteReportEntity
import ch.barrierelos.backend.helper.createWebsiteReportModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteReportConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteReportEntity()

    // then
    val model = createWebsiteReportModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteReportModel()

    // then
    val expected = createWebsiteReportEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteReportId, actual.websiteReportId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.report.reportId, actual.report.reportId)
    Assertions.assertEquals(expected.report.userFk, actual.report.userFk)
    Assertions.assertEquals(expected.report.reason, actual.report.reason)
    Assertions.assertEquals(expected.report.state, actual.report.state)
    Assertions.assertEquals(expected.report.modified, actual.report.modified)
    Assertions.assertEquals(expected.report.created, actual.report.created)
  }

  @Test
  fun `converts to models, when entities`()
  {
    // when
    val entities = setOf(
      createWebsiteReportEntity().apply { report.state = StateEnum.OPEN },
      createWebsiteReportEntity().apply { report.state = StateEnum.CLOSED }
    )

    // then
    val models = setOf(
      createWebsiteReportModel().apply { report.state = StateEnum.OPEN },
      createWebsiteReportModel().apply { report.state = StateEnum.CLOSED }
    )

    Assertions.assertEquals(models, entities.toModels())
  }

  @Test
  fun `converts to entities, when models`()
  {
    // when
    val models = setOf(
      createWebsiteReportModel().apply { report.state = StateEnum.OPEN },
      createWebsiteReportModel().apply { report.state = StateEnum.CLOSED }
    )

    // then
    val expected = setOf(
      createWebsiteReportEntity().apply { report.state = StateEnum.OPEN },
      createWebsiteReportEntity().apply { report.state = StateEnum.CLOSED }
    )
    val actual = models.toEntities()

    actual.shouldHaveSize(expected.size)
    actual.shouldHaveSingleElement { it.report.state == StateEnum.OPEN }
    actual.shouldHaveSingleElement { it.report.state == StateEnum.CLOSED }
  }
}
