package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createReportMessageEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class ReportMessageEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createReportMessageEntity()

    // then
    val actual = createReportMessageEntity()

    Assertions.assertEquals(expected.reportMessageId, actual.reportMessageId)
    Assertions.assertEquals(expected.reportFk, actual.reportFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.message, actual.message)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
