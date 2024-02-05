package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebsiteTagModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteTagModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteTagModel()

    // then
    val actual = createWebsiteTagModel()

    Assertions.assertEquals(expected, actual)
  }
}