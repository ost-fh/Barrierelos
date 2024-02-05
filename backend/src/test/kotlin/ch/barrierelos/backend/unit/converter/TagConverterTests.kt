package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntities
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.helper.createTagEntity
import ch.barrierelos.backend.helper.createTagModel
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class TagConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createTagEntity()

    // then
    val model = createTagModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createTagModel()

    // then
    val expected = createTagEntity()

    val actual = model.toEntity()

    Assertions.assertEquals(expected.tagId, actual.tagId)
    Assertions.assertEquals(expected.name, actual.name)
  }

  @Test
  fun `converts to models, when entities`()
  {
    // when
    val entities = setOf(
      createTagEntity().apply { name = "one" },
      createTagEntity().apply { name = "two" }
    )

    // then
    val models = setOf(
      createTagModel().apply { name = "one" },
      createTagModel().apply { name = "two" }
    )

    Assertions.assertEquals(models, entities.toModels())
  }

  @Test
  fun `converts to entities, when models`()
  {
    // when
    val models = setOf(
      createTagModel().apply { name = "one" },
      createTagModel().apply { name = "two" }
    )

    // then
    val expected = setOf(
      createTagEntity().apply { name = "one" },
      createTagEntity().apply { name = "two" }
    )
    val actual = models.toEntities()

    actual.shouldHaveSize(expected.size)
    actual.shouldHaveSingleElement { it.name == "one" }
    actual.shouldHaveSingleElement { it.name == "two" }
  }
}
