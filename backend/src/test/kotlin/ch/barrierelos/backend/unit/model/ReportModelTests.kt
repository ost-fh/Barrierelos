package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createReportModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createReportModel()

    // then
    val actual = createReportModel()

    Assertions.assertEquals(expected, actual)
  }
}
