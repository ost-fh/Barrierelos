package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebsiteStatisticModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteStatisticModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteStatisticModel()

    // then
    val actual = createWebsiteStatisticModel()

    Assertions.assertEquals(expected, actual)
  }
}
