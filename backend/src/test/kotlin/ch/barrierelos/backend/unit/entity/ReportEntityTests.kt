package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createReportEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createReportEntity()

    // then
    val actual = createReportEntity()

    Assertions.assertEquals(expected.reportId, actual.reportId)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.reason, actual.reason)
    Assertions.assertEquals(expected.state, actual.state)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
