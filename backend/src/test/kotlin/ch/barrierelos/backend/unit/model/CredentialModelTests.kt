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
    val expected = createCredentialModel(issuer = "issuer", subject = "subject")

    // then
    val actual = createCredentialModel(issuer = "issuer", subject = "subject")

    Assertions.assertEquals(expected, actual)
  }
}
