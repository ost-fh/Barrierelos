package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.repository.CredentialRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class CredentialRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var credentialRepository: CredentialRepository

  @Test
  fun `finds credential by userId, when user exists`()
  {
    // when
    val expected = createCredentialEntity()
    val user = createUserEntity()

    expected.userFk = entityManager.persist(user).userId
    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = credentialRepository.findByUserFk(expected.userFk)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.credentialId)
      Assertions.assertEquals(expected.credentialId, actual.credentialId)
      Assertions.assertEquals(expected.userFk, actual.userFk)
      Assertions.assertEquals(expected.password, actual.password)
      Assertions.assertEquals(expected.issuer, actual.issuer)
      Assertions.assertEquals(expected.subject, actual.subject)
      Assertions.assertEquals(expected.modified, actual.modified)
    }
  }

  @Test
  fun `cannot find credential by userId, when not exists`()
  {
    // when
    val expected = createCredentialEntity()

    // then
    val actual = credentialRepository.findByUserFk(expected.userFk)

    Assertions.assertNull(actual)
  }

  @Test
  fun `finds credential by id, when credential exists`()
  {
    // when
    val expected = createCredentialEntity()
    val user = createUserEntity()

    expected.userFk = entityManager.persist(user).userId
    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = credentialRepository.findById(expected.credentialId).orElse(null)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.credentialId)
      Assertions.assertEquals(expected.credentialId, actual.credentialId)
      Assertions.assertEquals(expected.userFk, actual.userFk)
      Assertions.assertEquals(expected.password, actual.password)
      Assertions.assertEquals(expected.issuer, actual.issuer)
      Assertions.assertEquals(expected.subject, actual.subject)
      Assertions.assertEquals(expected.modified, actual.modified)
    }
  }

  @Test
  fun `cannot find credential by id, when credential not exists`()
  {
    // when
    val expected = createCredentialEntity()

    // then
    val actual = credentialRepository.findById(expected.credentialId).orElse(null)

    Assertions.assertNull(actual)
  }

  @Test
  fun `cannot find credential by id, when wrong id`()
  {
    // when
    val expected = createCredentialEntity()
    val user = createUserEntity()

    expected.userFk = entityManager.persist(user).userId
    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = credentialRepository.findById(5000).orElse(null)

    Assertions.assertNull(actual)
  }
}