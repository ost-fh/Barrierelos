package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createCheckElementEntity
import ch.barrierelos.backend.helper.createCheckElementModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class CheckElementConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createCheckElementEntity()

    // then
    val model = createCheckElementModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createCheckElementModel()

    // then
    val expected = createCheckElementEntity()
    val actual = model.toEntity()

    Assertions.assertEquals(expected.checkElementId, actual.checkElementId)
    Assertions.assertEquals(expected.target, actual.target)
    Assertions.assertEquals(expected.html, actual.html)
    Assertions.assertEquals(expected.data, actual.data)
    Assertions.assertEquals(expected.issueDescription, actual.issueDescription)
    Assertions.assertEquals(expected.relatedElements, actual.relatedElements)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
