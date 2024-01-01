package ch.barrierelos.backend.integration.repository

import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Nested
abstract class UserRepositoryTests : RepositoryTests()
{
  @Autowired
  lateinit var userRepository: UserRepository

  @Test
  fun `finds user by username, when user exists`()
  {
    // when
    val expected = createUserEntity()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = userRepository.findByUsername(expected.username)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.userId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertEquals(expected.modified, actual.modified)
    }
  }

  @Test
  fun `cannot find user by username, when not exists`()
  {
    // when
    val expected = createUserEntity()

    // then
    val actual = userRepository.findByUsername(expected.username)

    Assertions.assertNull(actual)
  }

  @Test
  fun `cannot find user by username, when wrong username`()
  {
    // when
    val expected = createUserEntity()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = userRepository.findByUsername("nickname")

    Assertions.assertNull(actual)
  }

  @Test
  fun `finds user by id, when user exists`()
  {
    // when
    val expected = createUserEntity()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = userRepository.findById(expected.userId).orElse(null)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.userId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertEquals(expected.modified, actual.modified)
    }
  }

  @Test
  fun `cannot find user by id, when user not exists`()
  {
    // when
    val expected = createUserEntity()

    // then
    val actual = userRepository.findById(expected.userId).orElse(null)

    Assertions.assertNull(actual)
  }

  @Test
  fun `cannot find user by id, when wrong id`()
  {
    // when
    val expected = createUserEntity()

    entityManager.persist(expected)
    entityManager.flush()

    // then
    val actual = userRepository.findById(5000).orElse(null)

    Assertions.assertNull(actual)
  }

  @Test
  fun `finds user by issuer and subject, when user exists`()
  {
    // when
    val expected = createUserEntity()
    val credential = createCredentialEntity(issuer = "issuer", subject = "subject")

    credential.userFk = entityManager.persist(expected).userId
    entityManager.persist(credential)
    entityManager.flush()

    // then
    val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

    Assertions.assertNotNull(actual)

    if(actual != null)
    {
      Assertions.assertNotEquals(0, actual.userId)
      Assertions.assertEquals(expected.userId, actual.userId)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertEquals(expected.modified, actual.modified)
    }
  }

  @Test
  fun `cannot find user by issuer and subject, when user not exists`()
  {
    // when
    val credential = createCredentialEntity(issuer = "issuer", subject = "subject")

    // then
    val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

    Assertions.assertNull(actual)
  }

  @Test
  fun `cannot find user by issuer and subject, when wrong issuer`()
  {
    // when
    val expected = createUserEntity()
    val credential = createCredentialEntity(issuer = "issuer", subject = "subject")

    credential.userFk = entityManager.persist(expected).userId
    entityManager.persist(credential)
    entityManager.flush()

    // then
    val actual = userRepository.findByIssuerAndSubject("something", credential.subject!!)

    Assertions.assertNull(actual)
  }

  @Test
  fun `cannot find user by issuer and subject, when wrong subject`()
  {
    // when
    val expected = createUserEntity()
    val credential = createCredentialEntity(issuer = "issuer", subject = "subject")

    credential.userFk = entityManager.persist(expected).userId
    entityManager.persist(credential)
    entityManager.flush()

    // then
    val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, "something")

    Assertions.assertNull(actual)
  }
}