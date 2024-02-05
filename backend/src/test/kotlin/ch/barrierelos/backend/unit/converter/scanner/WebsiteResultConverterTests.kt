package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createWebsiteResultEntity
import ch.barrierelos.backend.helper.createWebsiteResultModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteResultConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createWebsiteResultEntity()

    // then
    val model = createWebsiteResultModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createWebsiteResultModel()

    // then
    val expected = createWebsiteResultEntity()
    val actual = model.toEntity()

    Assertions.assertEquals(expected.websiteResultId, actual.websiteResultId)
    Assertions.assertEquals(expected.modelVersion, actual.modelVersion)
    Assertions.assertEquals(expected.scanTimestamp, actual.scanTimestamp)
    Assertions.assertEquals(expected.scanStatus, actual.scanStatus)
    Assertions.assertEquals(expected.domain, actual.domain)
    Assertions.assertEquals(expected.errorMessage, actual.errorMessage)
    Assertions.assertEquals(expected.scanJob.scanJobId, actual.scanJob.scanJobId)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
