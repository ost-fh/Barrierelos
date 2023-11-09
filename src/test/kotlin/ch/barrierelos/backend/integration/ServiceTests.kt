package ch.barrierelos.backend.integration

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.InvalidCredentialsException
import ch.barrierelos.backend.exceptions.InvalidEmailException
import ch.barrierelos.backend.exceptions.NoRoleException
import ch.barrierelos.backend.exceptions.UserAlreadyExistsException
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createUserModel
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.security.Security
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
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
@ExtendWith(MockKExtension::class)
class ServiceTests
{
  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var userRepository: UserRepository

  @BeforeEach
  fun setUp()
  {
    // Delete all users
    this.userRepository.deleteAll()

    // Add admin user
    val adminUser = createUserEntity()
    adminUser.username = "admin"
    adminUser.roles = mutableSetOf(RoleEnum.ADMIN)
    this.userRepository.save(adminUser)

    // Add contributor user
    val contributorUser = createUserEntity()
    contributorUser.username = "contributor"
    contributorUser.roles = mutableSetOf(RoleEnum.CONTRIBUTOR)
    this.userRepository.save(contributorUser)
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
        val actual = userService.addUser(expected)

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertNull(actual.password)
        assertNull(actual.issuer)
        assertNull(actual.subject)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `adds admin user, given admin account`()
      {
        // when
        val expected = createUserModel()
        expected.roles = mutableSetOf(RoleEnum.ADMIN)

        // then
        val actual = userService.addUser(expected)

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertNull(actual.password)
        assertNull(actual.issuer)
        assertNull(actual.subject)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }

      @Test
      fun `cannot add user, when username already exists`()
      {
        // when
        val user = createUserModel()
        userService.addUser(user)

        // then
        assertThrows(UserAlreadyExistsException::class.java) {
          userService.addUser(user)
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
          userService.addUser(user)
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
          userService.addUser(user)
        }
      }

      @Test
      fun `cannot add user, when no credentials`()
      {
        // when
        val user = createUserModel()
        user.password = null
        user.issuer = null
        user.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.addUser(user)
        }
      }

      @Test
      fun `cannot add user, when invalid credentials`()
      {
        // when
        val user = createUserModel()
        user.password = ""
        user.issuer = ""
        user.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.addUser(user)
        }
      }

      @Test
      fun `cannot add admin user, given no account`()
      {
        // when
        val user = createUserModel()
        user.roles = mutableSetOf(RoleEnum.ADMIN)

        // then
        assertThrows(ResponseStatusException::class.java) {
          userService.addUser(user)
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
        assertThrows(ResponseStatusException::class.java) {
          userService.addUser(user)
        }
      }

      @Test
      fun `credentials not getting exposed, when adding user`()
      {
        // when
        val expected = createUserModel()

        // then
        val actual = userService.addUser(expected)

        assertNull(actual.password)
        assertNull(actual.issuer)
        assertNull(actual.subject)
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
        val expected = userService.addUser(createUserModel())

        expected.username = "username2"
        expected.firstname = "firstname2"
        expected.lastname = "lastname2"
        expected.email = "email2@gmail.com"
        expected.password = "password2"
        expected.issuer = "issuer2"
        expected.subject = "subject2"
        expected.roles = mutableSetOf(RoleEnum.VIEWER)

        // then
        val actual = userService.updateUser(expected, false)

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.firstname, actual.firstname)
        assertEquals(expected.lastname, actual.lastname)
        assertEquals(expected.email, actual.email)
        assertNull(actual.password)
        assertNull(actual.issuer)
        assertNull(actual.subject)
        assertEquals(expected.roles, actual.roles)
        assertEquals(expected.modified, actual.modified)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `updates user, when no credentials, given no change in credentials requested`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.password = null
        user.issuer = null
        user.subject = null

        // then
        assertDoesNotThrow {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `updates user, when invalid credentials, given no change in credentials requested`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.password = ""
        user.issuer = ""
        user.subject = ""

        // then
        assertDoesNotThrow {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when username already exists`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.username = "admin"

        // then
        assertThrows(UserAlreadyExistsException::class.java) {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when invalid email`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.email = "email"

        // then
        assertThrows(InvalidEmailException::class.java) {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when no roles`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.roles = mutableSetOf()

        // then
        assertThrows(NoRoleException::class.java) {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when no credentials, given change in credentials requested`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.password = null
        user.issuer = null
        user.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.updateUser(user, true)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update user, when invalid credentials, given change in credentials requested`()
      {
        // when
        val user = userService.addUser(createUserModel())
        user.password = ""
        user.issuer = ""
        user.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          userService.updateUser(user, true)
        }
      }

      @Test
      fun `cannot update user, given no account`()
      {
        // when
        val user = userService.addUser(createUserModel())

        // then
        assertThrows(ResponseStatusException::class.java) {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot update user, given wrong account`()
      {
        // when
        val user = userService.addUser(createUserModel())

        // then
        assertThrows(ResponseStatusException::class.java) {
          userService.updateUser(user, false)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `credentials not getting exposed, when updating user`()
      {
        // when
        val expected = userService.addUser(createUserModel())
        expected.password = "password2"

        // then
        val actual = userService.updateUser(expected, true)

        assertNull(actual.password)
        assertNull(actual.issuer)
        assertNull(actual.subject)
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
        assertThrows(ResponseStatusException::class.java) {
          userService.getUsers()
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot get users, given wrong account`()
      {
        assertThrows(ResponseStatusException::class.java) {
          userService.getUsers()
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `credentials not getting exposed, when getting users`()
      {
        // when
        val users = userService.getUsers().content

        // then
        users.forEach {user ->
          assertNull(user.password)
          assertNull(user.issuer)
          assertNull(user.subject)
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
        val expected = userService.addUser(createUserModel())

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
        assertThrows(ResponseStatusException::class.java) {
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
        assertThrows(ResponseStatusException::class.java) {
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

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `credentials not getting exposed, when getting user`()
      {
        val user = userService.getUser(Security.getUserId())

        assertNull(user.password)
        assertNull(user.issuer)
        assertNull(user.subject)
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
        val user = userService.addUser(createUserModel())
        val usersBefore = userService.getUsers().content

        // then
        assertDoesNotThrow {
          userService.deleteUser(user.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          userService.getUser(user.id)
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
      }

      @Test
      fun `cannot delete user, given no account`()
      {
        // when
        val user = userRepository.findAll().first().toModel()

        // then
        assertThrows(ResponseStatusException::class.java) {
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
        assertThrows(ResponseStatusException::class.java) {
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
}
