package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebsiteReportModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteReportModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteReportModel()

    // then
    val actual = createWebsiteReportModel()

    Assertions.assertEquals(expected, actual)
  }
}
