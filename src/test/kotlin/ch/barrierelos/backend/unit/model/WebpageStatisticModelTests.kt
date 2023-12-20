package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebpageStatisticModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageStatisticModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageStatisticModel()

    // then
    val actual = createWebpageStatisticModel()

    Assertions.assertEquals(expected, actual)
  }
}
