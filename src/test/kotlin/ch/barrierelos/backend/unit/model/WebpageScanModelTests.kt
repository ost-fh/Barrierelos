package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebpageScanModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageScanModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageScanModel()

    // then
    val actual = createWebpageScanModel()

    Assertions.assertEquals(expected, actual)
  }
}
