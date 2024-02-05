package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createCredentialEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class CredentialEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createCredentialEntity(issuer = "issuer", subject = "subject")

    // then
    val actual = createCredentialEntity(issuer = "issuer", subject = "subject")

    Assertions.assertEquals(expected.credentialId, actual.credentialId)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.password, actual.password)
    Assertions.assertEquals(expected.issuer, actual.issuer)
    Assertions.assertEquals(expected.subject, actual.subject)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
