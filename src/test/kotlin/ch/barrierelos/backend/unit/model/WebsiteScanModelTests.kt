package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebsiteScanModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteScanModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteScanModel()

    // then
    val actual = createWebsiteScanModel()

    Assertions.assertEquals(expected, actual)
  }
}
