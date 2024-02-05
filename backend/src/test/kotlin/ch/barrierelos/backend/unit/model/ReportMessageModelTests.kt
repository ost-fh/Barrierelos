package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createReportMessageModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportMessageModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createReportMessageModel()

    // then
    val actual = createReportMessageModel()

    Assertions.assertEquals(expected, actual)
  }
}
