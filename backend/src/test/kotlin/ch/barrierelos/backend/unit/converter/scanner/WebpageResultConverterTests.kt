package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createWebpageResultEntity
import ch.barrierelos.backend.helper.createWebpageResultModel
import ch.barrierelos.backend.helper.createWebsiteResultModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageResultConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebpageResultEntity()

    // then
    val model = createWebpageResultModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebpageResultModel()

    // then
    val expected = createWebpageResultEntity()
    val actual = model.toEntity(createWebsiteResultModel())

    Assertions.assertEquals(expected.webpageResultId, actual.webpageResultId)
    Assertions.assertEquals(expected.scanStatus, actual.scanStatus)
    Assertions.assertEquals(expected.url, actual.url)
    Assertions.assertEquals(expected.errorMessage, actual.errorMessage)
    Assertions.assertEquals(expected.rules, actual.rules)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
