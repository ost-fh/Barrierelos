package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebsiteModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteModel()

    // then
    val actual = createWebsiteModel()

    Assertions.assertEquals(expected, actual)
  }
}
