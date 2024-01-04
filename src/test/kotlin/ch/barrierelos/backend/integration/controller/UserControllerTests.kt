package ch.barrierelos.backend.integration.controller

import body
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exception.*
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.message.RegistrationMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.service.UserService
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.*
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@Nested
abstract class UserControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var userService: UserService

  var user = User(
    username = "user",
    firstname = "Peter",
    lastname = "Contributor",
    email = "contributor@barrierelos.ch",
    roles = mutableSetOf(RoleEnum.CONTRIBUTOR),
    modified = 5000,
  )
  val credential = createCredentialModel()
  lateinit var registration: RegistrationMessage
  lateinit var result: Result<User>

  @BeforeEach
  override fun beforeEach()
  {
    super.beforeEach()

    // Add user
    userRepository.save(user.toEntity()).toModel(user)

    // Add credential
    credential.userId = user.id
    credentialRepository.save(credential.toEntity()).toModel(credential)

    // Other
    registration = RegistrationMessage(user, credential)
    println(registration)
    result = Result(
      page = 0,
      size = 2,
      totalElements = 3,
      totalPages = 2,
      lastModified = 5000,
      content = listOf(
        admin,
        contributor,
        user,
      ),
    )
  }

  @Nested
  @DisplayName("Add User")
  inner class AddUserTests
  {
    @Test
    fun `uses correct media type`()
    {
      // when
      every { userService.addUser(user, credential) } returns user

      // then
      mockMvc.post("/user").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    fun `receives user, when provided in body`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } returns user

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.username") { value(user.username) }
          jsonPath("$.firstname") { value(user.firstname) }
          jsonPath("$.lastname") { value(user.lastname) }
          jsonPath("$.email") { value(user.email) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns user, when added, given account`()
    {
      // when
      val expected = registration.copy().apply { user.id = 0 }

      every { userService.addUser(expected.user, expected.credential) } returns expected.user

      // then
      val actual = mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<User>()

      Assertions.assertEquals(expected.user, actual)
    }

    @Test
    fun `returns user, when added, given no account`()
    {
      // when
      val expected = registration.copy().apply { user.id = 0 }

      every { userService.addUser(expected.user, expected.credential) } returns expected.user

      // then
      val actual = mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isCreated() }
      }.body<User>()

      Assertions.assertEquals(expected.user, actual)
    }

    @Test
    fun `responds with 400 bad request, when service throws InvalidCredentialsException`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } throws InvalidCredentialsException("")

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    fun `responds with 400 bad request, when service throws InvalidEmailException`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } throws InvalidEmailException("")

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    fun `responds with 400 bad request, when service throws NoRoleException`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } throws NoRoleException("")

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } throws NoAuthorizationException()

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { userService.addUser(user.apply { id = 0 }, credential) } throws AlreadyExistsException("")

      // then
      mockMvc.post("/user") {
        contentType = EXPECTED_MEDIA_TYPE
        content = registration.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    fun `responds with 415 unsupported media type, when no user provided in body`()
    {
      // when
      every { userService.addUser(user, credential) } returns user

      // then
      mockMvc.post("/user").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Update User")
  inner class UpdateUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userService.updateUser(admin) } returns admin

      // then
      mockMvc.put("/user/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives user, when provided in body`()
    {
      // when
      every { userService.updateUser(user.apply { id = 1 }) } returns user

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = user.toJson()
      }.andExpect {
        content {
          contentType(EXPECTED_MEDIA_TYPE)
          jsonPath("$.username") { value(user.username) }
          jsonPath("$.firstname") { value(user.firstname) }
          jsonPath("$.lastname") { value(user.lastname) }
          jsonPath("$.email") { value(user.email) }
        }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { userService.updateUser(admin) } returns admin

      // then
      mockMvc.put("/user/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `returns user, when updated, given account`()
    {
      // when
      val expected = user.copy().apply { id = 1 }

      every { userService.updateUser(expected) } returns expected

      // then
      val actual = mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }.body<User>()

      Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userService.updateUser(admin) } returns admin

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = admin.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 400 bad request, when service throws InvalidEmailException`()
    {
      // when
      every { userService.updateUser(admin.apply { id = 1 }) } throws InvalidEmailException("")

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = admin.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 400 bad request, when service throws NoRoleException`()
    {
      // when
      every { userService.updateUser(admin.apply { id = 1 }) } throws NoRoleException("")

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = admin.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userService.updateUser(admin.apply { id = 1 }) } throws NoAuthorizationException()

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = admin.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 409 conflict, when service throws AlreadyExistsException`()
    {
      // when
      every { userService.updateUser(admin.apply { id = 1 }) } throws AlreadyExistsException("")

      // then
      mockMvc.put("/user/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = admin.toJson()
      }.andExpect {
        status { isConflict() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no user provided in body`()
    {
      // when
      every { userService.updateUser(admin) } returns user

      // then
      mockMvc.put("/user/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }

  @Nested
  @DisplayName("Get Users")
  inner class GetUsersTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userService.getUsers() } returns result

      // then
      mockMvc.get("/user").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives default parameters, when default parameters provided`()
    {
      // when
      val defaultParameters = DefaultParameters(
        page = 0,
        size = 2,
        sort = "username",
        order = OrderEnum.ASC,
        modifiedAfter = 4000
      )

      every { userService.getUsers(defaultParameters) } returns result

      // then
      mockMvc.get("/user") {
        param("page", defaultParameters.page.toString())
        param("size", defaultParameters.size.toString())
        param("sort", defaultParameters.sort.toString())
        param("order", defaultParameters.order.toString())
        param("modifiedAfter", defaultParameters.modifiedAfter.toString())
      }.andExpect {
        haveParameterValue("page", defaultParameters.page.toString())
        haveParameterValue("size", defaultParameters.size.toString())
        haveParameterValue("sort", defaultParameters.sort.toString())
        haveParameterValue("order", defaultParameters.order.toString())
        haveParameterValue("modifiedAfter", defaultParameters.modifiedAfter.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets users, given account`()
    {
      // when
      every { userService.getUsers() } returns result

      // then
      val actual = mockMvc.get("/user").andExpect {
        status { isOk() }
      }.body<List<User>>()

      Assertions.assertEquals(result.content, actual)
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userService.getUsers() } returns result

      // then
      mockMvc.get("/user").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { userService.getUsers() } throws NoSuchElementException()

      // then
      mockMvc.get("/user").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Get User")
  inner class GetUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userService.getUser(1) } returns admin

      // then
      mockMvc.get("/user/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { userService.getUser(1) } returns admin

      // then
      mockMvc.get("/user/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets user, given account`()
    {
      // when
      every { userService.getUser(1) } returns admin

      // then
      val actual = mockMvc.get("/user/1").andExpect {
        status { isOk() }
      }.body<User>()

      Assertions.assertEquals(admin, actual)
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userService.getUser(1) } returns user

      // then
      mockMvc.get("/user/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userService.getUser(1) } throws NoAuthorizationException()

      // then
      mockMvc.get("/user/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { userService.getUser(1) } throws NoSuchElementException()

      // then
      mockMvc.get("/user/1").andExpect {
        status { isNotFound() }
      }
    }
  }

  @Nested
  @DisplayName("Delete User")
  inner class DeleteUserTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { userService.deleteUser(1) } returns Unit

      // then
      mockMvc.delete("/user/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { userService.deleteUser(1) } returns Unit

      // then
      mockMvc.delete("/user/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, given account`()
    {
      // when
      every { userService.deleteUser(1) } returns Unit

      // then
      mockMvc.delete("/user/1").andExpect {
        status { isOk() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { userService.deleteUser(1) } returns Unit

      // then
      mockMvc.delete("/user/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { userService.deleteUser(1) } throws NoAuthorizationException()

      // then
      mockMvc.delete("/user/1").andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 404 not found, when service throws NoSuchElementException`()
    {
      // when
      every { userService.deleteUser(1) } throws NoSuchElementException()

      // then
      mockMvc.delete("/user/1").andExpect {
        status { isNotFound() }
      }
    }
  }
}
