package ch.barrierelos.backend.integration

import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.dao.EmptyResultDataAccessException

@DataJpaTest
class RepositoryTests
{
  @Autowired
  lateinit var entityManager: TestEntityManager

  @Autowired
  lateinit var userRepository: UserRepository

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
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
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
      val actual = userRepository.findByUserId(expected.userId)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
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
      val actual = userRepository.findByUserId(expected.userId)

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
      val actual = userRepository.findByUserId(5000)

      assertNull(actual)
    }

    @Test
    fun `finds user by issuer and subject, when user exists`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(expected.issuer!!, expected.subject!!)

      assertNotNull(actual)

      if(actual != null)
      {
        assertNotEquals(0, actual.userId)
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }
    }

    @Test
    fun `cannot find user by issuer and subject, when user not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      val actual = userRepository.findByIssuerAndSubject(expected.issuer!!, expected.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong issuer`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject("something", expected.subject!!)

      assertNull(actual)
    }

    @Test
    fun `cannot find user by issuer and subject, when wrong subject`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val actual = userRepository.findByIssuerAndSubject(expected.issuer!!, "something")

      assertNull(actual)
    }

    @Test
    fun `gets password by id, when user exists`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      val password = userRepository.getPasswordById(expected.userId)

      assertEquals(expected.password, password)
    }

    @Test
    fun `cannot get password by id, when user not exists`()
    {
      // when
      val expected = createUserEntity()

      // then
      assertThrows(EmptyResultDataAccessException::class.java) {
        userRepository.getPasswordById(expected.userId)
      }
    }

    @Test
    fun `cannot get password by id, when wrong id`()
    {
      // when
      val expected = createUserEntity()

      entityManager.persist(expected)
      entityManager.flush()

      // then
      assertThrows(EmptyResultDataAccessException::class.java) {
        userRepository.getPasswordById(5000)
      }
    }
  }
}
