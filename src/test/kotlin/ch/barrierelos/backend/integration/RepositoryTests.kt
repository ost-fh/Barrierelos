package ch.barrierelos.backend.integration

import ch.barrierelos.backend.helper.*
import ch.barrierelos.backend.repository.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoryTests
{
  @Autowired
  lateinit var entityManager: TestEntityManager

  @Autowired
  lateinit var userRepository: UserRepository
  @Autowired
  lateinit var credentialRepository: CredentialRepository
  @Autowired
  lateinit var tagRepository: TagRepository

  @Nested
  inner class UserRepositoryTests
  {
    @Test
    fun `finds user by username, when user exists`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByUsername(expected.username)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by username, when not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = userRepository.findByUsername(expected.username)

      assertNull(actual)
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

      assertNull(actual)
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

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by id, when user not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = userRepository.findById(expected.userId).orElse(null)

      assertNull(actual)
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

      assertNull(actual)
    }

    @Test
    fun `finds user by issuer and subject, when user exists`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by issuer and subject, when user not exists`()
    {
      // when
      val credential = createCredentialEntity()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, credential.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong issuer`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject("something", credential.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong subject`()
    {
      // when
      val expected = createUserEntity()
      val credential = createCredentialEntity()

      credential.userFk = entityManager.persist(expected).userId
      entityManager.persist(credential)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(credential.issuer!!, "something")

      assertNull(actual)
    }
  }

  @Nested
  inner class CredentialRepositoryTests
  {
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

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.credentialId)
        assertEquals(expected.credentialId, actual.credentialId)
        assertEquals(expected.userFk, actual.userFk)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find credential by userId, when not exists`()
    {
      // when
      val expected = createCredentialEntity()

      // then
      val actual = credentialRepository.findByUserFk(expected.userFk)

      assertNull(actual)
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

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.credentialId)
        assertEquals(expected.credentialId, actual.credentialId)
        assertEquals(expected.userFk, actual.userFk)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find credential by id, when credential not exists`()
    {
      // when
      val expected = createCredentialEntity()

      // then
      val actual = credentialRepository.findById(expected.credentialId).orElse(null)

      assertNull(actual)
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

      assertNull(actual)
    }
  }

  @Nested
  inner class TagRepositoryTests
  {
    @Test
    fun `finds tags, when tags exist`()
    {
      // when
      entityManager.persist(createTagEntity().apply { name = "one" })
      entityManager.persist(createTagEntity().apply { name = "two" })
      entityManager.persist(createTagEntity().apply { name = "three" })
      entityManager.persist(createTagEntity().apply { name = "four" })
      entityManager.flush()

      // then
      val actual = tagRepository.findAll()

      assertNotNull(actual)
      actual.shouldNotBeEmpty()
      actual.shouldHaveSize(4)
    }

    @Test
    fun `cannot find tags, when no tags exist`()
    {
      // then
      val actual = tagRepository.findAll()

      assertNotNull(actual)
      actual.shouldBeEmpty()
      actual.shouldHaveSize(0)
    }

    @Test
    fun `find tag by name, when tag exists`()
    {
      // when
      val expected = createTagEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = tagRepository.findByName(expected.name)

      if(actual != null)
      {
        assertNotEquals(0, actual.tagId)
        assertEquals(expected.tagId, actual.tagId)
        assertEquals(expected.name, actual.name)
      }
    }

    @Test
    fun `cannot find tag by name, when tag not exists`()
    {
      // when
      val expected = createTagEntity()

      // then
      val actual = tagRepository.findByName(expected.name)

      assertNull(actual)
    }
  }
}
