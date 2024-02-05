package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createUserReportModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class UserReportModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createUserReportModel()

    // then
    val actual = createUserReportModel()

    Assertions.assertEquals(expected, actual)
  }
}
