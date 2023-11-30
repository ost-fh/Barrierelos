package ch.barrierelos.backend.integration

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.*
import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createUserModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.service.CredentialService
import ch.barrierelos.backend.service.UserService
import io.kotest.matchers.collections.*
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION
import org.springframework.security.test.context.support.WithUserDetails

@SpringBootTest
@ExtendWith(MockKExtension::class)
class ServiceTests
{
  @Autowired
  lateinit var userService: UserService
  @Autowired
  lateinit var credentialService: CredentialService

  @Autowired
  lateinit var userRepository: UserRepository
  @Autowired
  lateinit var credentialRepository: CredentialRepository

  @BeforeEach
  fun setUp()
  {
    // Delete all users
    userRepository.deleteAll()

    // Add admin user
    val adminUser = createUserEntity()
    adminUser.username = "admin"
    adminUser.roles = mutableSetOf(RoleEnum.ADMIN)
    userRepository.save(adminUser)

    // Add contributor user
    val contributorUser = createUserEntity()
    contributorUser.username = "contributor"
    contributorUser.roles = mutableSetOf(RoleEnum.CONTRIBUTOR)
    userRepository.save(contributorUser)

    // Add admin credential
    val adminCredential = createCredentialEntity()
    adminCredential.userFk = adminUser.userId
    credentialRepository.save(adminCredential)

    // Add contributor credential
    val contributorCredential = createCredentialEntity()
    contributorCredential.userFk = contributorUser.userId
    credentialRepository.save(contributorCredential)
  }

  @Nested
  inner class UserServiceTests
  {
    @Nested
    inner class AddUserTests
    {
      @Test
      fun `adds user, when roles allowed`()
      {
        // when
        val expected = createUserModel()
        expected.roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER)

        // then
        val actual = userService.addUser(expected.copy(), createCredentialModel())

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertNotEquals(expected.modified, actual.modified)
        assertNotEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `adds admin user, given admin account`()
      {
        // when
        val expected = createUserModel()
        expected.roles = mutableSetOf(RoleEnum.ADMIN)

        // then
        val actual = userService.addUser(expected.copy(), createCredentialModel())

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertNotEquals(expected.modified, actual.modified)
        assertNotEquals(expected.created, actual.created)
      }

      @Test
      fun `cannot add user, when username already exists`()
      {
        // when
        val user = createUserModel()
        userService.addUser(user, createCredentialModel())

        // then
        assertThrows(UserAlreadyExistsException::class.java) {
          userService.addUser(user, createCredentialModel())
        }
      }

      @Test
      fun `cannot add user, when invalid email`()
      {
        // when
        val user = createUserModel()
        user.email = "email"

        // then
        assertThrows(InvalidEmailException::class.java) {
          userService.addUser(user, createCredentialModel())
        }
      }

      @Test
      fun `cannot add user, when no roles`()
      {
        // when
        val user = createUserModel()
        user.roles = mutableSetOf()

        // then
        assertThrows(NoRoleException::class.java) {
          userService.addUser(user, createCredentialModel())
        }
      }

      @Test
      fun `cannot add user, when no credentials`()
      {
        // when
        val user = createUserModel()
        val credential = createCredentialModel()
        credential.password = null
        credential.issuer = null
        credential.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.addUser(user, credential)
        }
      }

      @Test
      fun `cannot add user, when invalid credentials`()
      {
        // when
        val user = createUserModel()
        val credential = createCredentialModel()
        credential.password = ""
        credential.issuer = ""
        credential.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.addUser(user, credential)
        }
      }

      @Test
      fun `cannot add admin user, given no account`()
      {
        // when
        val user = createUserModel()
        user.roles = mutableSetOf(RoleEnum.ADMIN)

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.addUser(user, createCredentialModel())
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot add admin user, given wrong account`()
      {
        // when
        val user = createUserModel()
        user.roles = mutableSetOf(RoleEnum.ADMIN)

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.addUser(user, createCredentialModel())
        }
      }
    }

    @Nested
    inner class UpdateUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `updates user, given admin user`()
      {
        // when
        val expected = userService.addUser(createUserModel(), createCredentialModel())
        expected.username = "username2"
        expected.firstname = "firstname2"
        expected.lastname = "lastname2"
        expected.email = "email2@gmail.com"
        expected.roles = mutableSetOf(RoleEnum.VIEWER)

        // then
        val actual = userService.updateUser(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.roles, actual.roles)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when username already exists`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())
        user.username = "admin"

        // then
        assertThrows(UserAlreadyExistsException::class.java) {
          userService.updateUser(user)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when invalid email`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())
        user.email = "email"

        // then
        assertThrows(InvalidEmailException::class.java) {
          userService.updateUser(user)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when no roles`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())
        user.roles = mutableSetOf()

        // then
        assertThrows(NoRoleException::class.java) {
          userService.updateUser(user)
        }
      }

      @Test
      fun `cannot update user, given no account`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.updateUser(user)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot update user, given wrong account`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.updateUser(user)
        }
      }
    }

    @Nested
    inner class GetUsersTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets users, given admin account`()
      {
        // when
        val users = userService.getUsers().content

        // then
        users.shouldNotBeEmpty()
        users.shouldHaveSize(2)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets users with headers, when no parameters`()
      {
        // when
        val users = userService.getUsers()

        // then
        users.page.shouldBe(0)
        users.size.shouldBe(2)
        users.totalElements.shouldBe(2)
        users.totalPages.shouldBe(1)
        users.count.shouldBe(2)
        users.lastModified.shouldBeGreaterThan(0)
        users.content.shouldHaveSize(2)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets users with headers, when with parameters`()
      {
        // when
        val users = userService.getUsers(DefaultParameters(
          page = 1,
          size = 1,
          sort = "username",
          order = OrderEnum.ASC
        ))

        // then
        users.page.shouldBe(1)
        users.size.shouldBe(1)
        users.totalElements.shouldBe(2)
        users.totalPages.shouldBe(2)
        users.count.shouldBe(2)
        users.lastModified.shouldBeGreaterThan(0)
        users.content.shouldHaveSize(1)
      }

      @Test
      fun `cannot get users, given no account`()
      {
        assertThrows(NoAuthorizationException::class.java) {
          userService.getUsers()
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot get users, given wrong account`()
      {
        assertThrows(NoAuthorizationException::class.java) {
          userService.getUsers()
        }
      }
    }

    @Nested
    inner class GetUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets user, given admin account`()
      {
        // when
        val expected = userService.addUser(createUserModel(), createCredentialModel())

        // then
        val actual = userService.getUser(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `gets user, given correct account`()
      {
        val actual = userService.getUser(Security.getUserId())

        assertEquals(Security.getUserId(), actual.id)
        assertEquals(Security.getUsername(), actual.username)
      }

      @Test
      fun `cannot get user, given no account`()
      {
        // when
        val user = userRepository.findAll().first().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.getUser(user.id)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot get user, given wrong account`()
      {
        // when
        val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.getUser(user.id)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot get user, when user not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          userService.getUser(5000000)
        }
      }
    }

    @Nested
    inner class DeleteUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `deletes user, given admin account`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())
        val usersBefore = userService.getUsers().content

        // then
        assertDoesNotThrow {
          userService.deleteUser(user.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          userService.getUser(user.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          credentialService.getCredential(user.id)
        }

        val usersAfter = userService.getUsers().content

        usersBefore.shouldContain(user)
        usersAfter.shouldNotContain(user)
        usersBefore.shouldContainAll(usersAfter)
        usersAfter.shouldNotContainAll(usersBefore)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `deletes user, given correct account`()
      {
        // when
        val user = userService.getUser(Security.getUserId())

        // then
        assertDoesNotThrow {
          userService.deleteUser(user.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          userService.getUser(user.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          credentialService.getCredential(user.id)
        }
      }

      @Test
      fun `cannot delete user, given no account`()
      {
        // when
        val user = userRepository.findAll().first().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.deleteUser(user.id)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot delete user, given wrong account`()
      {
        // when
        val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          userService.deleteUser(user.id)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot delete user, when user not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          userService.deleteUser(5000000)
        }
      }
    }
  }

  @Nested
  inner class CredentialServiceTests
  {
    @Nested
    inner class AddCredentialTests
    {
      @Test
      fun `adds credential, when valid credentials`()
      {
        // when
        val expected = createCredentialModel()

        // then
        val actual = credentialService.addCredential(expected.copy())

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertNotEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertNotEquals(expected.modified, actual.modified)
        assertNotEquals(expected.created, actual.created)
      }

      @Test
      fun `cannot add credential, when no credentials`()
      {
        // when
        val credential = createCredentialModel()
        credential.password = null
        credential.issuer = null
        credential.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.addCredential(credential)
        }
      }

      @Test
      fun `cannot add credential, when invalid credentials`()
      {
        // when
        val credential = createCredentialModel()
        credential.password = ""
        credential.issuer = ""
        credential.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.addCredential(credential)
        }
      }
    }

    @Nested
    inner class UpdateCredentialTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `updates credential, given admin user`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel().apply { userId = 10000 })
        expected.password = "password2"
        expected.issuer = "issuer2"
        expected.subject = "subject2"

        // then
        val actual = credentialService.updateCredential(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertNotEquals(expected.password, actual.password)
        assertEquals(expected.issuer, actual.issuer)
        assertEquals(expected.subject, actual.subject)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update credential, when no credentials`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel())
        expected.password = null
        expected.issuer = null
        expected.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.updateCredential(expected)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update credential, when invalid credentials`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel())
        expected.password = ""
        expected.issuer = ""
        expected.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.updateCredential(expected)
        }
      }

      @Test
      fun `cannot update user, given no account`()
      {
        // when
        val credential = credentialService.addCredential(createCredentialModel())

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.updateCredential(credential)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot update user, given wrong account`()
      {
        // when
        val credential = credentialService.addCredential(createCredentialModel())

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.updateCredential(credential)
        }
      }
    }

    @Nested
    inner class GetCredentialTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets credential, given admin account`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel().apply { userId = 5000 })

        // then
        val actual = credentialService.getCredential(expected.userId)

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `gets credential, given correct account`()
      {
        val actual = credentialService.getCredential(Security.getUserId())

        assertEquals(Security.getUserId(), actual.userId)
      }

      @Test
      fun `cannot get credential, given no account`()
      {
        // when
        val user = userRepository.findAll().first().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.getCredential(user.id)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot get credential, given wrong account`()
      {
        // when
        val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.getCredential(user.id)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot get credential, when credential not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          credentialService.getCredential(5000000)
        }
      }
    }

    @Nested
    inner class DeleteCredentialTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `deletes credential, given admin account`()
      {
        // when
        val credential = credentialService.addCredential(createCredentialModel())

        // then
        assertDoesNotThrow {
          credentialService.deleteCredential(credential.userId)
        }

        assertThrows(NoSuchElementException::class.java) {
          credentialService.getCredential(credential.userId)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `deletes credential, given correct account`()
      {
        // when
        val credential = credentialService.getCredential(Security.getUserId())

        // then
        assertDoesNotThrow {
          credentialService.deleteCredential(credential.userId)
        }

        assertThrows(NoSuchElementException::class.java) {
          credentialService.getCredential(credential.userId)
        }
      }

      @Test
      fun `cannot delete credential, given no account`()
      {
        // when
        val credential = credentialRepository.findAll().first().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.deleteCredential(credential.userId)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot delete credential, given wrong account`()
      {
        // when
        val credential = credentialRepository.findAll().find { it.credentialId != Security.getUserId() }!!.toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          credentialService.deleteCredential(credential.userId)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot delete credential, when credential not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          credentialService.deleteCredential(5000000)
        }
      }
    }
  }
}
