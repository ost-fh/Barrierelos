package ch.barrierelos.backend.unit

import ch.barrierelos.backend.helper.createUserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ModelTests
{
  @Nested
  inner class UserTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createUserModel()

      // then
      val actual = createUserModel()

      assertEquals(expected, actual)
    }
  }
}
