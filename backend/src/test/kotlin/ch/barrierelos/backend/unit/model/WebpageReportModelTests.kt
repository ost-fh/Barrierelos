package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createWebpageReportModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageReportModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageReportModel()

    // then
    val actual = createWebpageReportModel()

    Assertions.assertEquals(expected, actual)
  }
}
