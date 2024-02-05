package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createTagModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class TagModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createTagModel()

    // then
    val actual = createTagModel()

    Assertions.assertEquals(expected, actual)
  }
}
