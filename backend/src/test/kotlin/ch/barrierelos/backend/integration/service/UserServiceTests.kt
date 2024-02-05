package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exception.*
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.helper.createUserModel
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.service.UserService
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class UserServiceTests : ServiceTests()
{
  @Autowired
  lateinit var userService: UserService

  @Nested
  @DisplayName("Add User")
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

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `adds admin user, given admin account`()
    {
      // when
      val expected = createUserModel()
      expected.roles = mutableSetOf(RoleEnum.ADMIN)

      // then
      val actual = userService.addUser(expected.copy(), createCredentialModel())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    fun `cannot add user, when username already exists`()
    {
      // when
      val user = createUserModel()
      userService.addUser(user, createCredentialModel())

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        userService.addUser(user, createCredentialModel())
      }
    }

    @Test
    fun `cannot add user, when invalid username`()
    {
      // when
      val user = createUserModel()
      user.username = "username@barrierelos.ch"

      // then
      Assertions.assertThrows(InvalidUsernameException::class.java) {
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
      Assertions.assertThrows(InvalidEmailException::class.java) {
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
      Assertions.assertThrows(NoRoleException::class.java) {
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
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
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
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
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
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.addUser(user, createCredentialModel())
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot add admin user, given wrong account`()
    {
      // when
      val user = createUserModel()
      user.roles = mutableSetOf(RoleEnum.ADMIN)

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.addUser(user, createCredentialModel())
      }
    }
  }

  @Nested
  @DisplayName("Update User")
  inner class UpdateUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
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

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertEquals(expected.username, actual.username)
      Assertions.assertEquals(expected.firstname, actual.firstname)
      Assertions.assertEquals(expected.lastname, actual.lastname)
      Assertions.assertEquals(expected.email, actual.email)
      Assertions.assertEquals(expected.roles, actual.roles)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, when username already exists`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())
      user.username = "admin"

      // then
      Assertions.assertThrows(AlreadyExistsException::class.java) {
        userService.updateUser(user)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, when invalid username`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())
      user.username = "username@barrierelos.ch"

      // then
      Assertions.assertThrows(InvalidUsernameException::class.java) {
        userService.updateUser(user)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, when invalid email`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())
      user.email = "email"

      // then
      Assertions.assertThrows(InvalidEmailException::class.java) {
        userService.updateUser(user)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, when no roles`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())
      user.roles = mutableSetOf()

      // then
      Assertions.assertThrows(NoRoleException::class.java) {
        userService.updateUser(user)
      }
    }

    @Test
    fun `cannot update user, given no account`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.updateUser(user)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, given wrong account`()
    {
      // when
      val user = userService.addUser(createUserModel(), createCredentialModel())

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.updateUser(user)
      }
    }
  }

  @Nested
  @DisplayName("Get Users")
  inner class GetUsersTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets users, given admin account`()
    {
      // when
      val users = userService.getUsers().content

      // then
      users.shouldNotBeEmpty()
      users.shouldHaveSize(4)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets users with headers, when no parameters`()
    {
      // when
      val users = userService.getUsers()

      // then
      users.page.shouldBe(0)
      users.size.shouldBe(4)
      users.totalElements.shouldBe(4)
      users.totalPages.shouldBe(1)
      users.lastModified.shouldBeGreaterThan(0)
      users.content.shouldHaveSize(4)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets users with headers, when with parameters`()
    {
      // when
      val users = userService.getUsers(
        DefaultParameters(
          page = 1,
          size = 1,
          sort = "username",
          order = OrderEnum.ASC
        )
      )

      // then
      users.page.shouldBe(1)
      users.size.shouldBe(1)
      users.totalElements.shouldBe(4)
      users.totalPages.shouldBe(4)
      users.lastModified.shouldBeGreaterThan(0)
      users.content.shouldHaveSize(1)
    }

    @Test
    fun `cannot get users, given no account`()
    {
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.getUsers()
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get users, given wrong account`()
    {
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.getUsers()
      }
    }
  }

  @Nested
  @DisplayName("Get User")
  inner class GetUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user, given admin account`()
    {
      // when
      val expected = userService.addUser(createUserModel(), createCredentialModel())

      // then
      val actual = userService.getUser(expected.id)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("moderator", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user, given moderator account`()
    {
      // when
      val user = userService.getUser(moderator.id)

      // then
      Assertions.assertEquals(moderator, user)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user, given contributor account`()
    {
      // when
      val user = userService.getUser(contributor.id)

      // then
      Assertions.assertEquals(contributor, user)
    }

    @Test
    @WithUserDetails("viewer", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user, given viewer account`()
    {
      // when
      val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.getUser(user.id)
      }
    }

    @Test
    fun `cannot get user, given no account`()
    {
      // when
      val user = userRepository.findAll().first().toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.getUser(user.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get user, when user not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        userService.getUser(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete User")
  inner class DeleteUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes user, given admin account`()
    {
      // when
      val user = userService.getUser(Security.getUserId())

      // then
      Assertions.assertDoesNotThrow {
        userService.deleteUser(user.id)
      }

      userService.getUser(user.id).should { it.deleted }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes user, given correct account`()
    {
      // when
      val user = userService.getUser(Security.getUserId())

      // then
      Assertions.assertDoesNotThrow {
        userService.deleteUser(user.id)
      }

      userService.getUser(user.id).should { it.deleted }
    }

    @Test
    fun `cannot delete user, given no account`()
    {
      // when
      val user = userRepository.findAll().first().toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.deleteUser(user.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete user, given wrong account`()
    {
      // when
      val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        userService.deleteUser(user.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete user, when user not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        userService.deleteUser(5000000)
      }
    }
  }
}
