package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createTagEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class TagEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createTagEntity()

    // then
    val actual = createTagEntity()

    Assertions.assertEquals(expected.tagId, actual.tagId)
    Assertions.assertEquals(expected.name, actual.name)
  }
}
