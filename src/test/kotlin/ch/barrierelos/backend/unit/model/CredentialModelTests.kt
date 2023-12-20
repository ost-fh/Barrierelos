package ch.barrierelos.backend.unit.model

import ch.barrierelos.backend.helper.createCredentialModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class CredentialModelTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createCredentialModel()

    // then
    val actual = createCredentialModel()

    Assertions.assertEquals(expected, actual)
  }
}
