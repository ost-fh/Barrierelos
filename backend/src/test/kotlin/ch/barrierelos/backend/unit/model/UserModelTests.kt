package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createUserModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

abstract class UserModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createUserModel()

    // then
    val actual = createUserModel()

    Assertions.assertEquals(expected, actual)
  }
}
