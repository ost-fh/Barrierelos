package ch.barrierelos.backend.integration

import body
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exceptions.*
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.message.RegistrationMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.service.CredentialService
import ch.barrierelos.backend.service.UserService
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.*
import java.nio.charset.StandardCharsets.UTF_8

private val EXPECTED_MEDIA_TYPE = MediaType(APPLICATION_JSON, UTF_8)

@AutoConfigureMockMvc
@SpringBootTest
class ControllerTests(@Autowired val mockMvc: MockMvc)
{
  @MockkBean
  lateinit var userService: UserService
  @MockkBean
  lateinit var credentialService: CredentialService

  @Autowired
  lateinit var userRepository: UserRepository
  @Autowired
  lateinit var credentialRepository: CredentialRepository

  private fun createAdminUser(id: Long) = User(
    id = id,
    username = "admin",
    firstname = "Hans",
    lastname = "Admin",
    email = "admin@barrierelos.ch",
    roles = mutableSetOf(RoleEnum.ADMIN),
    modified = 5000,
  )

  private fun createContributorUser(id: Long) = User(
    id = id,
    username = "contributor",
    firstname = "Peter",
    lastname = "Contributor",
    email = "contributor@barrierelos.ch",
    roles = mutableSetOf(RoleEnum.CONTRIBUTOR),
    modified = 5000,
  )

  private fun createCredential(userId: Long) = createCredentialModel().apply { this.userId = userId }

  private val user = createContributorUser(0)
  private val adminUser = createAdminUser(1)
  private val contributorUser = createContributorUser(2)
  private val credential = createCredential(user.id)
  private val adminCredential = createCredential(adminUser.id)
  private val contributorCredential = createCredential(contributorUser.id)
  private val registration = RegistrationMessage(
    user = user,
    credential = credential,
  )
  private val result = Result(
    page = 0,
    size = 2,
    totalElements = 3,
    totalPages = 2,
    count = 3,
    lastModified = 5000,
    content = listOf(
      adminUser,
      contributorUser,
      user,
    ),
  )

  @BeforeEach
  fun setUp()
  {
    // Delete all users
    userRepository.deleteAll()

    // Add admin user
    val adminId = userRepository.save(adminUser.toEntity()).userId

    // Add contributor user
    val contributorId = userRepository.save(contributorUser.toEntity()).userId

    // Add admin credential
    credentialRepository.save(adminCredential.apply { userId = adminId }.toEntity())

    // Add contributor credential
    credentialRepository.save(contributorCredential.apply { userId = contributorId }.toEntity())
  }

  @Nested
  inner class UserControllerTests
  {
    @Nested
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
        val registration = RegistrationMessage(
          user = user,
          credential = credential
        )

        every { userService.addUser(user, credential) } returns user

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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns user, when added, given account`()
      {
        // when
        val expected = RegistrationMessage(
          user = createContributorUser(0),
          credential = createCredential(0)
        )

        every { userService.addUser(expected.user, expected.credential) } returns expected.user

        // then
        val actual = mockMvc.post("/user") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<User>()

        assertEquals(expected.user, actual)
      }

      @Test
      fun `returns user, when added, given no account`()
      {
        // when
        val expected = RegistrationMessage(
          user = createContributorUser(0),
          credential = createCredential(0)
        )

        every { userService.addUser(expected.user, expected.credential) } returns expected.user

        // then
        val actual = mockMvc.post("/user") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<User>()

        assertEquals(expected.user, actual)
      }

      @Test
      fun `responds with 400 bad request, when service throws InvalidCredentialsException`()
      {
        // when
        every { userService.addUser(user, credential) } throws InvalidCredentialsException("")

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
        every { userService.addUser(user, credential) } throws InvalidEmailException("")

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
        every { userService.addUser(user, credential) } throws NoRoleException("")

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
        every { userService.addUser(user, credential) } throws NoAuthorizationException()

        // then
        mockMvc.post("/user") {
          contentType = EXPECTED_MEDIA_TYPE
          content = registration.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      fun `responds with 409 conflict, when service throws UserAlreadyExistsException`()
      {
        // when
        every { userService.addUser(user, credential) } throws UserAlreadyExistsException("")

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
    inner class UpdateUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { userService.updateUser(adminUser) } returns adminUser

        // then
        mockMvc.put("/user/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives user, when provided in body`()
      {
        // when
        val user = createContributorUser(1)

        every { userService.updateUser(user) } returns user

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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { userService.updateUser(adminUser) } returns adminUser

        // then
        mockMvc.put("/user/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns user, when updated, given account`()
      {
        // when
        val expected = createContributorUser(1)

        every { userService.updateUser(expected) } returns expected

        // then
        val actual = mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<User>()

        assertEquals(expected, actual)
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { userService.updateUser(adminUser) } returns adminUser

        // then
        mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminUser.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 400 bad request, when service throws InvalidEmailException`()
      {
        // when
        every { userService.updateUser(adminUser) } throws InvalidEmailException("")

        // then
        mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminUser.toJson()
        }.andExpect {
          status { isBadRequest() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 400 bad request, when service throws NoRoleException`()
      {
        // when
        every { userService.updateUser(adminUser) } throws NoRoleException("")

        // then
        mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminUser.toJson()
        }.andExpect {
          status { isBadRequest() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { userService.updateUser(adminUser) } throws NoAuthorizationException()

        // then
        mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminUser.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws UserAlreadyExistsException`()
      {
        // when
        every { userService.updateUser(adminUser) } throws UserAlreadyExistsException("")

        // then
        mockMvc.put("/user/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminUser.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no user provided in body`()
      {
        // when
        every { userService.updateUser(adminUser) } returns user

        // then
        mockMvc.put("/user/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetUsersTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets users, given account`()
      {
        // when
        every { userService.getUsers() } returns result

        // then
        val actual = mockMvc.get("/user").andExpect {
          status { isOk() }
        }.body<List<User>>()

        assertEquals(result.content, actual)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
    inner class GetUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { userService.getUser(1) } returns adminUser

        // then
        mockMvc.get("/user/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { userService.getUser(1) } returns adminUser

        // then
        mockMvc.get("/user/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `gets user, given account`()
      {
        // when
        every { userService.getUser(1) } returns adminUser

        // then
        val actual = mockMvc.get("/user/1").andExpect {
          status { isOk() }
        }.body<User>()

        assertEquals(adminUser, actual)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
    inner class DeleteUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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

  @Nested
  inner class CredentialControllerTests
  {
    @Nested
    inner class UpdateCredentialTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { credentialService.updateCredential(adminCredential) } returns adminCredential

        // then
        mockMvc.put("/credential/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives credential, when provided in body`()
      {
        // when
        val credential = createCredential(1)
        credential.id = 1
        credential.password = "password"
        credential.issuer = "issuer"
        credential.subject = "subject"

        every { credentialService.updateCredential(credential) } returns credential

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = credential.toJson()
        }.andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { credentialService.updateCredential(adminCredential) } returns adminCredential

        // then
        mockMvc.put("/credential/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 200 ok, when updated, given account`()
      {
        // when
        val expected = createCredential(1).apply { id = 1 }

        every { credentialService.updateCredential(expected) } returns expected

        println(expected.toJson())

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { credentialService.updateCredential(adminCredential) } returns adminCredential

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminCredential.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 400 bad request, when service throws InvalidCredentialsException`()
      {
        // when
        every { credentialService.updateCredential(adminCredential.copy(userId = 1)) } throws InvalidCredentialsException("")

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminCredential.toJson()
        }.andExpect {
          status { isBadRequest() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { credentialService.updateCredential(adminCredential.copy(userId = 1)) } throws NoAuthorizationException()

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = adminCredential.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no credential provided in body`()
      {
        // when
        every { credentialService.updateCredential(adminCredential.copy(userId = 1)) } returns credential

        // then
        mockMvc.put("/credential/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }
  }
}
