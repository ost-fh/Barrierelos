package ch.barrierelos.backend.unit.converter.scanner

import ch.barrierelos.backend.converter.scanner.toEntity
import ch.barrierelos.backend.converter.scanner.toModel
import ch.barrierelos.backend.helper.createRuleEntity
import ch.barrierelos.backend.helper.createRuleModel
import ch.barrierelos.backend.helper.createWebpageResultEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class RuleConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createRuleEntity(createWebpageResultEntity())

    // then
    val model = createRuleModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createRuleModel()

    // then
    val expected = createRuleEntity(createWebpageResultEntity())
    val actual = model.toEntity(expected.webpageResult)

    Assertions.assertEquals(expected.ruleId, actual.ruleId)
    Assertions.assertEquals(expected.code, actual.code)
    Assertions.assertEquals(expected.axeUrl, actual.axeUrl)
    Assertions.assertEquals(expected.description, actual.description)
    Assertions.assertEquals(expected.wcagReferences, actual.wcagReferences)
    Assertions.assertEquals(expected.checks, actual.checks)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
