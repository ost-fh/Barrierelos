package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createCredentialModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class CredentialConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createCredentialEntity()

    // then
    val model = createCredentialModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createCredentialModel()

    // then
    val expected = createCredentialEntity()
    val actual = model.toEntity()

    Assertions.assertEquals(expected.credentialId, actual.credentialId)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.password, actual.password)
    Assertions.assertEquals(expected.issuer, actual.issuer)
    Assertions.assertEquals(expected.subject, actual.subject)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
