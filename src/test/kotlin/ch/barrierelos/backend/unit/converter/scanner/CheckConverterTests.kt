package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createCheckEntity
import ch.barrierelos.backend.helper.createCheckModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class CheckConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createCheckEntity()

    // then
    val model = createCheckModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createCheckModel()

    // then
    val expected = createCheckEntity()
    val actual = model.toEntity(expected.rule)

    Assertions.assertEquals(expected.checkId, actual.checkId)
    Assertions.assertEquals(expected.rule.ruleId, actual.rule.ruleId)
    Assertions.assertEquals(expected.code, actual.code)
    Assertions.assertEquals(expected.impact, actual.impact)
    Assertions.assertEquals(expected.type, actual.type)
    Assertions.assertEquals(expected.testedCount, actual.testedCount)
    Assertions.assertEquals(expected.passedCount, actual.passedCount)
    Assertions.assertEquals(expected.violatedCount, actual.violatedCount)
    Assertions.assertEquals(expected.incompleteCount, actual.incompleteCount)
    Assertions.assertEquals(expected.incompleteElements, actual.incompleteElements)
    Assertions.assertEquals(expected.incompleteElements, actual.incompleteElements)
    Assertions.assertEquals(expected.violatingElements, actual.violatingElements)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
