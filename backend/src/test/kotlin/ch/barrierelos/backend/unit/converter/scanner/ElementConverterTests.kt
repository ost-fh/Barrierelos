package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createElementEntity
import ch.barrierelos.backend.helper.createElementModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ElementConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createElementEntity()

    // then
    val model = createElementModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createElementModel()

    // then
    val expected = createElementEntity()
    val actual = model.toEntity(expected.checkElement)

    Assertions.assertEquals(expected.elementId, actual.elementId)
    Assertions.assertEquals(expected.checkElement, actual.checkElement)
    Assertions.assertEquals(expected.target, actual.target)
    Assertions.assertEquals(expected.html, actual.html)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
