package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebpageModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageModel()

    // then
    val actual = createWebpageModel()

    Assertions.assertEquals(expected, actual)
  }
}
