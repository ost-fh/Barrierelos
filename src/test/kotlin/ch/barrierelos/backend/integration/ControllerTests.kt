package ch.barrierelos.backend.integration

import body
import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.*
import ch.barrierelos.backend.exceptions.*
import ch.barrierelos.backend.helper.*
import ch.barrierelos.backend.message.RegistrationMessage
import ch.barrierelos.backend.model.*
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.service.*
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.AfterEach
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
  @MockkBean
  lateinit var tagService: TagService
  @MockkBean
  lateinit var websiteService: WebsiteService
  @MockkBean
  lateinit var webpageService: WebpageService
  @MockkBean
  lateinit var websiteStatisticService: WebsiteStatisticService
  @MockkBean
  lateinit var webpageStatisticService: WebpageStatisticService
  @MockkBean
  lateinit var websiteScanService: WebsiteScanService
  @MockkBean
  lateinit var webpageScanService: WebpageScanService
  @MockkBean
  lateinit var reportService: ReportService

  @Autowired
  lateinit var userRepository: UserRepository
  @Autowired
  lateinit var credentialRepository: CredentialRepository

  lateinit var admin: User
  lateinit var moderator: User
  lateinit var contributor: User
  lateinit var viewer: User

  @BeforeEach
  fun beforeEach()
  {
    // Add admin user
    val adminUser = createUserEntity()
    adminUser.username = "admin"
    adminUser.roles = mutableSetOf(RoleEnum.ADMIN)
    admin = userRepository.save(adminUser).toModel()

    // Add moderator user
    val moderatorUser = createUserEntity()
    moderatorUser.username = "moderator"
    moderatorUser.roles = mutableSetOf(RoleEnum.MODERATOR)
    moderator = userRepository.save(moderatorUser).toModel()

    // Add contributor user
    val contributorUser = createUserEntity()
    contributorUser.username = "contributor"
    contributorUser.roles = mutableSetOf(RoleEnum.CONTRIBUTOR)
    contributor = userRepository.save(contributorUser).toModel()

    // Add viewer user
    val viewerUser = createUserEntity()
    viewerUser.username = "viewer"
    viewerUser.roles = mutableSetOf(RoleEnum.VIEWER)
    viewer = userRepository.save(viewerUser).toModel()

    // Add admin credential
    val adminCredential = createCredentialEntity()
    adminCredential.userFk = adminUser.userId
    credentialRepository.save(adminCredential)

    // Add moderator credential
    val moderatorCredential = createCredentialEntity()
    moderatorCredential.userFk = moderatorUser.userId
    credentialRepository.save(moderatorCredential)

    // Add contributor credential
    val contributorCredential = createCredentialEntity()
    contributorCredential.userFk = contributorUser.userId
    credentialRepository.save(contributorCredential)

    // Add viewer credential
    val viewerCredential = createCredentialEntity()
    viewerCredential.userFk = viewerUser.userId
    credentialRepository.save(viewerCredential)
  }

  @AfterEach
  fun afterEach()
  {
    userRepository.deleteAll()
    credentialRepository.deleteAll()
  }

  @Nested
  inner class UserControllerTests
  {
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
    fun beforeEach()
    {
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
        count = 3,
        lastModified = 5000,
        content = listOf(
          admin,
          contributor,
          user,
        ),
      )
    }

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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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

        assertEquals(expected.user, actual)
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

        assertEquals(expected.user, actual)
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
    inner class UpdateUserTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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

        assertEquals(expected, actual)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
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
        every { userService.getUser(1) } returns admin

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
        every { userService.getUser(1) } returns admin

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
        every { userService.getUser(1) } returns admin

        // then
        val actual = mockMvc.get("/user/1").andExpect {
          status { isOk() }
        }.body<User>()

        assertEquals(admin, actual)
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
    val credential = createCredentialModel()

    @Nested
    inner class UpdateCredentialTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { credentialService.updateCredential(credential) } returns credential

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
        val credential = credential.copy().apply {
          id = 1
          userId = 1
          password = "password"
          issuer = "issuer"
          subject = "subject"
        }

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
        every { credentialService.updateCredential(credential) } returns credential

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
        val expected = credential.copy().apply {
          id = 1
          userId = 1
        }

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
        every { credentialService.updateCredential(credential) } returns credential

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = credential.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 400 bad request, when service throws InvalidCredentialsException`()
      {
        // when
        every { credentialService.updateCredential(credential.copy(userId = 1)) } throws InvalidCredentialsException("")

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = credential.toJson()
        }.andExpect {
          status { isBadRequest() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { credentialService.updateCredential(credential.copy(userId = 1)) } throws NoAuthorizationException()

        // then
        mockMvc.put("/credential/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = credential.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no credential provided in body`()
      {
        // when
        every { credentialService.updateCredential(credential.copy(userId = 1)) } returns credential

        // then
        mockMvc.put("/credential/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }
  }

  @Nested
  inner class TagControllerTests
  {
    private val tag = Tag(
      id = 0,
      name = "name",
    )

    @Nested
    inner class AddTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { tagService.addTag(tag) } returns tag

        // then
        mockMvc.post("/tag").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives tag, when provided in body`()
      {
        // when
        every { tagService.addTag(tag) } returns tag

        // then
        mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.name") { value(tag.name) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns tag, when added, given admin account`()
      {
        // when
        val expected = tag.copy()

        every { tagService.addTag(expected) } returns expected

        // then
        val actual = mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Tag>()

        assertEquals(expected, actual)
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { tagService.addTag(tag) } returns tag

        // then
        mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given wrong account`()
      {
        // when
        every { tagService.addTag(tag) } returns tag

        // then
        mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { tagService.addTag(tag) } throws NoAuthorizationException()

        // then
        mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { tagService.addTag(tag) } throws AlreadyExistsException("")

        // then
        mockMvc.post("/tag") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no tag provided in body`()
      {
        // when
        every { tagService.addTag(tag) } returns tag

        // then
        mockMvc.post("/tag").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { tagService.updateTag(tag) } returns tag

        // then
        mockMvc.put("/tag/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives tag, when provided in body`()
      {
        // when
        every { tagService.updateTag(tag.apply { id = 1 }) } returns tag

        // then
        mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.name") { value(tag.name) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { tagService.updateTag(tag) } returns tag

        // then
        mockMvc.put("/tag/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns tag, when updated, given admin account`()
      {
        // when
        val expected = tag.copy()

        every { tagService.updateTag(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Tag>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403, given wrong account`()
      {
        // when
        every { tagService.updateTag(tag) } returns tag

        // then
        mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { tagService.updateTag(tag) } returns tag

        // then
        mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { tagService.updateTag(tag.apply { id = 1 }) } throws NoAuthorizationException()

        // then
        mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { tagService.updateTag(tag.apply { id = 1 }) } throws AlreadyExistsException("")

        // then
        mockMvc.put("/tag/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = tag.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no tag provided in body`()
      {
        // when
        every { tagService.updateTag(tag) } returns tag

        // then
        mockMvc.put("/tag/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetTagsTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { tagService.getTags() } returns mutableSetOf(tag)

        // then
        mockMvc.get("/tag").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `gets tags`()
      {
        // when
        val expected = mutableSetOf(tag.copy())

        every { tagService.getTags() } returns expected

        // then
        val actual = mockMvc.get("/tag").andExpect {
          status { isOk() }
        }.body<Set<Tag>>()

        assertEquals(expected, actual)
      }
    }

    @Nested
    inner class GetTagTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { tagService.getTag(1) } returns tag

        // then
        mockMvc.get("/tag/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { tagService.getTag(1) } returns tag

        // then
        mockMvc.get("/tag/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets tag`()
      {
        // when
        val expected = tag.copy()

        every { tagService.getTag(1) } returns expected

        // then
        val actual = mockMvc.get("/tag/1").andExpect {
          status { isOk() }
        }.body<Tag>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { tagService.getTag(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/tag/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { tagService.deleteTag(1) } returns Unit

        // then
        mockMvc.delete("/tag/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { tagService.deleteTag(1) } returns Unit

        // then
        mockMvc.delete("/tag/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { tagService.deleteTag(1) } returns Unit

        // then
        mockMvc.delete("/tag/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { tagService.deleteTag(1) } returns Unit

        // then
        mockMvc.delete("/tag/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given wrong account`()
      {
        // when
        every { tagService.deleteTag(1) } returns Unit

        // then
        mockMvc.delete("/tag/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { tagService.deleteTag(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/tag/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebsiteControllerTests
  {
    private val websiteMessage = createWebsiteMessage()
    private val website = createWebsiteModel()

    @Nested
    inner class AddWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        mockMvc.post("/website").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website, when provided in body`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.user.username") { value(website.user.username) }
            jsonPath("$.domain") { value(website.domain) }
            jsonPath("$.url") { value(website.url) }
            jsonPath("$.category") { value(website.category.toString()) }
            jsonPath("$.tags[0].tag.name") { value(website.tags.first().tag.name) }
            jsonPath("$.modified") { value(website.modified) }
            jsonPath("$.created") { value(website.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns website, when added, given admin account`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        val actual = mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Website>()

        assertEquals(website, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `returns website, when added, given moderator account`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        val actual = mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Website>()

        assertEquals(website, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `returns website, when added, given contributor account`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        val actual = mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Website>()

        assertEquals(website, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } throws NoAuthorizationException()

        // then
        mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } throws AlreadyExistsException("")

        // then
        mockMvc.post("/website") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteMessage.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website provided in body`()
      {
        // when
        every { websiteService.addWebsite(websiteMessage) } returns website

        // then
        mockMvc.post("/website").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteService.updateWebsite(website) } returns website

        // then
        mockMvc.put("/website/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website, when provided in body`()
      {
        // when
        every { websiteService.updateWebsite(website.apply {
          id = 1
          status = StatusEnum.READY
        }) } returns website

        // then
        mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = website.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.user.username") { value(website.user.username) }
            jsonPath("$.domain") { value(website.domain) }
            jsonPath("$.url") { value(website.url) }
            jsonPath("$.category") { value(website.category.toString()) }
            jsonPath("$.status") { value(website.status.toString()) }
            jsonPath("$.tags[0].tag.name") { value(website.tags.first().tag.name) }
            jsonPath("$.modified") { value(website.modified) }
            jsonPath("$.created") { value(website.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteService.updateWebsite(website) } returns website

        // then
        mockMvc.put("/website/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns website, when updated, given admin account`()
      {
        // when
        val expected = website.copy()

        every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Website>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `returns website, when updated, given moderator account`()
      {
        // when
        val expected = website.copy()

        every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Website>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `returns website, when updated, given contributor account`()
      {
        // when
        val expected = website.copy()

        every { websiteService.updateWebsite(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Website>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteService.updateWebsite(website) } returns website

        // then
        mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = website.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteService.updateWebsite(website) } returns website

        // then
        mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = website.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteService.updateWebsite(website.apply { id = 1 }) } throws NoAuthorizationException()

        // then
        mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = website.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { websiteService.updateWebsite(website.apply { id = 1 }) } throws AlreadyExistsException("")

        // then
        mockMvc.put("/website/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = website.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website provided in body`()
      {
        // when
        every { websiteService.updateWebsite(website) } returns website

        // then
        mockMvc.put("/website/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebsitesTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          website
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteService.getWebsites() } returns result

        // then
        mockMvc.get("/website").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "domain",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { websiteService.getWebsites(defaultParameters = defaultParameters) } returns result

        // then
        mockMvc.get("/website") {
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
      fun `gets websites`()
      {
        // when
        every { websiteService.getWebsites() } returns result

        // then
        val actual = mockMvc.get("/website").andExpect {
          status { isOk() }
        }.body<List<Website>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebsiteTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteService.getWebsite(1) } returns website

        // then
        mockMvc.get("/website/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteService.getWebsite(1) } returns website

        // then
        mockMvc.get("/website/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets website`()
      {
        // when
        val expected = website.copy()

        every { websiteService.getWebsite(1) } returns expected

        // then
        val actual = mockMvc.get("/website/1").andExpect {
          status { isOk() }
        }.body<Website>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { websiteService.getWebsite(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/website/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given moderator account`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteService.deleteWebsite(1) } returns Unit

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteService.deleteWebsite(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/website/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebpageControllerTests
  {
    private val webpageMessage = createWebpageMessage()
    private val webpage = createWebpageModel()

    @Nested
    inner class AddWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        mockMvc.post("/webpage").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage, when provided in body`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.user.username") { value(webpage.user.username) }
            jsonPath("$.displayUrl") { value(webpage.displayUrl) }
            jsonPath("$.url") { value(webpage.url) }
            jsonPath("$.modified") { value(webpage.modified) }
            jsonPath("$.created") { value(webpage.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns webpage, when added, given admin account`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        val actual = mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Webpage>()

        assertEquals(webpage, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `returns webpage, when added, given moderator account`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        val actual = mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Webpage>()

        assertEquals(webpage, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `returns webpage, when added, given contributor account`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        val actual = mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Webpage>()

        assertEquals(webpage, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } throws NoAuthorizationException()

        // then
        mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } throws AlreadyExistsException("")

        // then
        mockMvc.post("/webpage") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageMessage.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage provided in body`()
      {
        // when
        every { webpageService.addWebpage(webpageMessage) } returns webpage

        // then
        mockMvc.post("/webpage").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageService.updateWebpage(webpage) } returns webpage

        // then
        mockMvc.put("/webpage/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage, when provided in body`()
      {
        // when
        every { webpageService.updateWebpage(webpage.apply {
          id = 1
          status = StatusEnum.READY
        }) } returns webpage

        // then
        mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpage.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.user.username") { value(webpage.user.username) }
            jsonPath("$.displayUrl") { value(webpage.displayUrl) }
            jsonPath("$.url") { value(webpage.url) }
            jsonPath("$.status") { value(webpage.status.toString()) }
            jsonPath("$.modified") { value(webpage.modified) }
            jsonPath("$.created") { value(webpage.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageService.updateWebpage(webpage) } returns webpage

        // then
        mockMvc.put("/webpage/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns webpage, when updated, given admin account`()
      {
        // when
        val expected = webpage.copy()

        every { webpageService.updateWebpage(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Webpage>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `returns webpage, when updated, given moderator account`()
      {
        // when
        val expected = webpage.copy()

        every { webpageService.updateWebpage(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Webpage>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `returns webpage, when updated, given contributor account`()
      {
        // when
        val expected = webpage.copy()

        every { webpageService.updateWebpage(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Webpage>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageService.updateWebpage(webpage) } returns webpage

        // then
        mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpage.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageService.updateWebpage(webpage) } returns webpage

        // then
        mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageService.updateWebpage(webpage.apply { id = 1 }) } throws NoAuthorizationException()

        // then
        mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpage.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 409 conflict, when service throws AlreadyExistsException`()
      {
        // when
        every { webpageService.updateWebpage(webpage.apply { id = 1 }) } throws AlreadyExistsException("")

        // then
        mockMvc.put("/webpage/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpage.toJson()
        }.andExpect {
          status { isConflict() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage provided in body`()
      {
        // when
        every { webpageService.updateWebpage(webpage) } returns webpage

        // then
        mockMvc.put("/webpage/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebpagesTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          webpage
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageService.getWebpages() } returns result

        // then
        mockMvc.get("/webpage").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "domain",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { webpageService.getWebpages(defaultParameters = defaultParameters) } returns result

        // then
        mockMvc.get("/webpage") {
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
      fun `gets webpages`()
      {
        // when
        every { webpageService.getWebpages() } returns result

        // then
        val actual = mockMvc.get("/webpage").andExpect {
          status { isOk() }
        }.body<List<Webpage>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebpageTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageService.getWebpage(1) } returns webpage

        // then
        mockMvc.get("/webpage/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageService.getWebpage(1) } returns webpage

        // then
        mockMvc.get("/webpage/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets webpage`()
      {
        // when
        val expected = webpage.copy()

        every { webpageService.getWebpage(1) } returns expected

        // then
        val actual = mockMvc.get("/webpage/1").andExpect {
          status { isOk() }
        }.body<Webpage>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { webpageService.getWebpage(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/webpage/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given moderator account`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageService.deleteWebpage(1) } returns Unit

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageService.deleteWebpage(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/webpage/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebsiteStatisticControllerTests
  {
    private val websiteStatistic = createWebsiteStatisticModel()

    @Nested
    inner class AddWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website statistic, when provided in body`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.score") { value(websiteStatistic.score) }
            jsonPath("$.modified") { value(websiteStatistic.modified) }
            jsonPath("$.created") { value(websiteStatistic.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns website statistic, when added, given admin account`()
      {
        // when
        val expected = websiteStatistic.copy()

        every { websiteStatisticService.addWebsiteStatistic(expected) } returns expected

        // then
        val actual = mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<WebsiteStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } throws NoAuthorizationException()

        // then
        mockMvc.post("/website-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website statistic provided in body`()
      {
        // when
        every { websiteStatisticService.addWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.post("/website-statistic").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website statistic, when provided in body`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic.apply { id = 1 }) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.score") { value(websiteStatistic.score) }
            jsonPath("$.modified") { value(websiteStatistic.modified) }
            jsonPath("$.created") { value(websiteStatistic.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns website statistic, when updated, given admin account`()
      {
        // when
        val expected = websiteStatistic.copy()

        every { websiteStatisticService.updateWebsiteStatistic(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<WebsiteStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic.apply { id = 1 }) } throws NoAuthorizationException()

        // then
        mockMvc.put("/website-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website statistic provided in body`()
      {
        // when
        every { websiteStatisticService.updateWebsiteStatistic(websiteStatistic) } returns websiteStatistic

        // then
        mockMvc.put("/website-statistic/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebsiteStatisticsTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          websiteStatistic
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteStatisticService.getWebsiteStatistics() } returns result

        // then
        mockMvc.get("/website-statistic").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "score",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { websiteStatisticService.getWebsiteStatistics(defaultParameters) } returns result

        // then
        mockMvc.get("/website-statistic") {
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
      fun `gets website statistics`()
      {
        // when
        every { websiteStatisticService.getWebsiteStatistics() } returns result

        // then
        val actual = mockMvc.get("/website-statistic").andExpect {
          status { isOk() }
        }.body<List<WebsiteStatistic>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebsiteStatisticTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteStatisticService.getWebsiteStatistic(1) } returns websiteStatistic

        // then
        mockMvc.get("/website-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteStatisticService.getWebsiteStatistic(1) } returns websiteStatistic

        // then
        mockMvc.get("/website-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets website statistic`()
      {
        // when
        val expected = websiteStatistic.copy()

        every { websiteStatisticService.getWebsiteStatistic(1) } returns expected

        // then
        val actual = mockMvc.get("/website-statistic/1").andExpect {
          status { isOk() }
        }.body<WebsiteStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { websiteStatisticService.getWebsiteStatistic(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/website-statistic/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } returns Unit

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteStatisticService.deleteWebsiteStatistic(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/website-statistic/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebpageStatisticControllerTests
  {
    private val webpageStatistic = createWebpageStatisticModel()

    @Nested
    inner class AddWebpageStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage statistic, when provided in body`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.score") { value(webpageStatistic.score) }
            jsonPath("$.modified") { value(webpageStatistic.modified) }
            jsonPath("$.created") { value(webpageStatistic.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns webpage statistic, when added, given admin account`()
      {
        // when
        val expected = webpageStatistic.copy()

        every { webpageStatisticService.addWebpageStatistic(expected) } returns expected

        // then
        val actual = mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<WebpageStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } throws NoAuthorizationException()

        // then
        mockMvc.post("/webpage-statistic") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage statistic provided in body`()
      {
        // when
        every { webpageStatisticService.addWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.post("/webpage-statistic").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebpageStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage statistic, when provided in body`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic.apply { id = 1 }) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.score") { value(webpageStatistic.score) }
            jsonPath("$.modified") { value(webpageStatistic.modified) }
            jsonPath("$.created") { value(webpageStatistic.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns webpage statistic, when updated, given admin account`()
      {
        // when
        val expected = webpageStatistic.copy()

        every { webpageStatisticService.updateWebpageStatistic(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<WebpageStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic.apply { id = 1 }) } throws NoAuthorizationException()

        // then
        mockMvc.put("/webpage-statistic/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageStatistic.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage statistic provided in body`()
      {
        // when
        every { webpageStatisticService.updateWebpageStatistic(webpageStatistic) } returns webpageStatistic

        // then
        mockMvc.put("/webpage-statistic/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebpageStatisticsTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          webpageStatistic
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageStatisticService.getWebpageStatistics() } returns result

        // then
        mockMvc.get("/webpage-statistic").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "score",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { webpageStatisticService.getWebpageStatistics(defaultParameters) } returns result

        // then
        mockMvc.get("/webpage-statistic") {
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
      fun `gets webpage statistics`()
      {
        // when
        every { webpageStatisticService.getWebpageStatistics() } returns result

        // then
        val actual = mockMvc.get("/webpage-statistic").andExpect {
          status { isOk() }
        }.body<List<WebpageStatistic>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebpageStatisticTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageStatisticService.getWebpageStatistic(1) } returns webpageStatistic

        // then
        mockMvc.get("/webpage-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageStatisticService.getWebpageStatistic(1) } returns webpageStatistic

        // then
        mockMvc.get("/webpage-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets webpage statistic`()
      {
        // when
        val expected = webpageStatistic.copy()

        every { webpageStatisticService.getWebpageStatistic(1) } returns expected

        // then
        val actual = mockMvc.get("/webpage-statistic/1").andExpect {
          status { isOk() }
        }.body<WebpageStatistic>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { webpageStatisticService.getWebpageStatistic(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/webpage-statistic/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebpageStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } returns Unit

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageStatisticService.deleteWebpageStatistic(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/webpage-statistic/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebsiteScanControllerTests
  {
    private val websiteScan = createWebsiteScanModel()

    @Nested
    inner class AddWebsiteScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteScanService.addWebsiteScan(websiteScan) } returns websiteScan

        // then
        mockMvc.post("/website-scan").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website scan, when provided in body`()
      {
        // when
        every { websiteScanService.addWebsiteScan(websiteScan) } returns websiteScan

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.websiteId") { value(websiteScan.websiteId) }
            jsonPath("$.userId") { value(websiteScan.userId) }
            jsonPath("$.websiteStatisticId") { value(websiteScan.websiteStatisticId) }
            jsonPath("$.websiteResultId") { value(websiteScan.websiteResultId) }
            jsonPath("$.modified") { value(websiteScan.modified) }
            jsonPath("$.created") { value(websiteScan.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns website scan, when added, given admin account`()
      {
        // when
        val expected = websiteScan.copy()

        every { websiteScanService.addWebsiteScan(expected) } returns expected

        // then
        val actual = mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<WebsiteScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } throws NoAuthorizationException()

        // then
        mockMvc.post("/website-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website scan provided in body`()
      {
        // when
        every { websiteScanService.addWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.post("/website-scan").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebsiteScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(websiteScan) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives website scan, when provided in body`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(websiteScan.apply { id = 1 }) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.websiteId") { value(websiteScan.websiteId) }
            jsonPath("$.userId") { value(websiteScan.userId) }
            jsonPath("$.websiteStatisticId") { value(websiteScan.websiteStatisticId) }
            jsonPath("$.websiteResultId") { value(websiteScan.websiteResultId) }
            jsonPath("$.modified") { value(websiteScan.modified) }
            jsonPath("$.created") { value(websiteScan.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(websiteScan) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns website scan, when updated, given admin account`()
      {
        // when
        val expected = websiteScan.copy()

        every { websiteScanService.updateWebsiteScan(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<WebsiteScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } throws NoAuthorizationException()

        // then
        mockMvc.put("/website-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = websiteScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no website scan provided in body`()
      {
        // when
        every { websiteScanService.updateWebsiteScan(any()) } returns websiteScan

        // then
        mockMvc.put("/website-scan/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebsiteScansTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          websiteScan
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteScanService.getWebsiteScans() } returns result

        // then
        mockMvc.get("/website-scan").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "score",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { websiteScanService.getWebsiteScans(defaultParameters) } returns result

        // then
        mockMvc.get("/website-scan") {
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
      fun `gets website scans`()
      {
        // when
        every { websiteScanService.getWebsiteScans() } returns result

        // then
        val actual = mockMvc.get("/website-scan").andExpect {
          status { isOk() }
        }.body<List<WebsiteScan>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebsiteScanTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { websiteScanService.getWebsiteScan(1) } returns websiteScan

        // then
        mockMvc.get("/website-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteScanService.getWebsiteScan(1) } returns websiteScan

        // then
        mockMvc.get("/website-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets website scan`()
      {
        // when
        val expected = websiteScan.copy()

        every { websiteScanService.getWebsiteScan(1) } returns expected

        // then
        val actual = mockMvc.get("/website-scan/1").andExpect {
          status { isOk() }
        }.body<WebsiteScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { websiteScanService.getWebsiteScan(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/website-scan/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebsiteScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } returns Unit

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { websiteScanService.deleteWebsiteScan(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/website-scan/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class WebpageScanControllerTests
  {
    private val webpageScan = createWebpageScanModel()

    @Nested
    inner class AddWebpageScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageScanService.addWebpageScan(webpageScan) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage scan, when provided in body`()
      {
        // when
        every { webpageScanService.addWebpageScan(webpageScan) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.webpageId") { value(webpageScan.webpageId) }
            jsonPath("$.userId") { value(webpageScan.userId) }
            jsonPath("$.webpageStatisticId") { value(webpageScan.webpageStatisticId) }
            jsonPath("$.webpageResultId") { value(webpageScan.webpageResultId) }
            jsonPath("$.modified") { value(webpageScan.modified) }
            jsonPath("$.created") { value(webpageScan.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns webpage scan, when added, given admin account`()
      {
        // when
        val expected = webpageScan.copy()

        every { webpageScanService.addWebpageScan(expected) } returns expected

        // then
        val actual = mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<WebpageScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } throws NoAuthorizationException()

        // then
        mockMvc.post("/webpage-scan") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage scan provided in body`()
      {
        // when
        every { webpageScanService.addWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.post("/webpage-scan").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateWebpageScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageScanService.updateWebpageScan(webpageScan) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives webpage scan, when provided in body`()
      {
        // when
        every { webpageScanService.updateWebpageScan(webpageScan.apply { id = 1 }) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.webpageId") { value(webpageScan.webpageId) }
            jsonPath("$.userId") { value(webpageScan.userId) }
            jsonPath("$.webpageStatisticId") { value(webpageScan.webpageStatisticId) }
            jsonPath("$.webpageResultId") { value(webpageScan.webpageResultId) }
            jsonPath("$.modified") { value(webpageScan.modified) }
            jsonPath("$.created") { value(webpageScan.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageScanService.updateWebpageScan(webpageScan) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns webpage scan, when updated, given admin account`()
      {
        // when
        val expected = webpageScan.copy()

        every { webpageScanService.updateWebpageScan(expected.apply { id = 1 }) } returns expected

        // then
        val actual = mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<WebpageScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } throws NoAuthorizationException()

        // then
        mockMvc.put("/webpage-scan/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = webpageScan.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no webpage scan provided in body`()
      {
        // when
        every { webpageScanService.updateWebpageScan(any()) } returns webpageScan

        // then
        mockMvc.put("/webpage-scan/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetWebpageScansTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          webpageScan
        ),
      )

      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageScanService.getWebpageScans() } returns result

        // then
        mockMvc.get("/webpage-scan").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "score",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { webpageScanService.getWebpageScans(defaultParameters) } returns result

        // then
        mockMvc.get("/webpage-scan") {
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
      fun `gets webpage scans`()
      {
        // when
        every { webpageScanService.getWebpageScans() } returns result

        // then
        val actual = mockMvc.get("/webpage-scan").andExpect {
          status { isOk() }
        }.body<List<WebpageScan>>()

        assertEquals(result.content, actual)
      }
    }

    @Nested
    inner class GetWebpageScanTests
    {
      @Test
      fun `uses correct media type`()
      {
        // when
        every { webpageScanService.getWebpageScan(1) } returns webpageScan

        // then
        mockMvc.get("/webpage-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageScanService.getWebpageScan(1) } returns webpageScan

        // then
        mockMvc.get("/webpage-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      fun `gets webpage scan`()
      {
        // when
        val expected = webpageScan.copy()

        every { webpageScanService.getWebpageScan(1) } returns expected

        // then
        val actual = mockMvc.get("/webpage-scan/1").andExpect {
          status { isOk() }
        }.body<WebpageScan>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { webpageScanService.getWebpageScan(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/webpage-scan/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteWebpageScanTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } returns Unit

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { webpageScanService.deleteWebpageScan(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/webpage-scan/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }

  @Nested
  inner class ReportControllerTests
  {
    private val report = createReportModel()

    @Nested
    inner class AddReportTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { reportService.addReport(any()) } returns report

        // then
        mockMvc.post("/report").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives report, when provided in body`()
      {
        // when
        every { reportService.addReport(report.apply {
          reason = ReasonEnum.INCORRECT
          state = StateEnum.CLOSED
        }) } returns report

        // then
        mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.userId") { value(report.userId) }
            jsonPath("$.reason") { value(report.reason.toString()) }
            jsonPath("$.state") { value(report.state.toString()) }
            jsonPath("$.modified") { value(report.modified) }
            jsonPath("$.created") { value(report.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `returns report, when added, given admin account`()
      {
        // when
        val expected = report.copy()

        every { reportService.addReport(expected) } returns expected

        // then
        val actual = mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `returns report, when added, given moderator account`()
      {
        // when
        val expected = report.copy()

        every { reportService.addReport(expected) } returns expected

        // then
        val actual = mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `returns report, when added, given contributor account`()
      {
        // when
        val expected = report.copy()

        every { reportService.addReport(expected) } returns expected

        // then
        val actual = mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isCreated() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { reportService.addReport(any()) } returns report

        // then
        mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { reportService.addReport(any()) } returns report

        // then
        mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { reportService.addReport(any()) } throws NoAuthorizationException()

        // then
        mockMvc.post("/report") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no report provided in body`()
      {
        // when
        every { reportService.addReport(any()) } returns report

        // then
        mockMvc.post("/report").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class UpdateReportTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives report, when provided in body`()
      {
        // when
        every { reportService.updateReport(report.apply {
          id = 1
          reason = ReasonEnum.INCORRECT
          state = StateEnum.CLOSED
        }) } returns report

        // then
        mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          content {
            contentType(EXPECTED_MEDIA_TYPE)
            jsonPath("$.userId") { value(report.userId) }
            jsonPath("$.reason") { value(report.reason.toString()) }
            jsonPath("$.state") { value(report.state.toString()) }
            jsonPath("$.modified") { value(report.modified) }
            jsonPath("$.created") { value(report.created) }
          }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `returns report, when updated, given admin account`()
      {
        // when
        val expected = report.copy().apply { id = 1 }

        every { reportService.updateReport(expected) } returns expected

        // then
        val actual = mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `returns report, when updated, given moderator account`()
      {
        // when
        val expected = report.copy().apply { id = 1 }

        every { reportService.updateReport(expected) } returns expected

        // then
        val actual = mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = expected.toJson()
        }.andExpect {
          status { isOk() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { reportService.updateReport(any()) } throws NoAuthorizationException()

        // then
        mockMvc.put("/report/1") {
          contentType = EXPECTED_MEDIA_TYPE
          content = report.toJson()
        }.andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `responds with 415 unsupported media type, when no report provided in body`()
      {
        // when
        every { reportService.updateReport(any()) } returns report

        // then
        mockMvc.put("/report/1").andExpect {
          status { isUnsupportedMediaType() }
        }
      }
    }

    @Nested
    inner class GetReportsTests
    {
      private val result = Result(
        page = 0,
        size = 1,
        totalElements = 1,
        totalPages = 1,
        count = 1,
        lastModified = 5000,
        content = listOf(
          report
        ),
      )

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        mockMvc.get("/report").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `receives default parameters, when default parameters provided`()
      {
        // when
        val defaultParameters = DefaultParameters(
          page = 0,
          size = 2,
          sort = "score",
          order = OrderEnum.ASC,
          modifiedAfter = 4000
        )

        every { reportService.getReports(defaultParameters) } returns result

        // then
        mockMvc.get("/report") {
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
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `gets reports, given admin account`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        val actual = mockMvc.get("/report").andExpect {
          status { isOk() }
        }.body<List<Report>>()

        assertEquals(result.content, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `gets reports, given moderator account`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        val actual = mockMvc.get("/report").andExpect {
          status { isOk() }
        }.body<List<Report>>()

        assertEquals(result.content, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `gets reports, given contributor account`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        val actual = mockMvc.get("/report").andExpect {
          status { isOk() }
        }.body<List<Report>>()

        assertEquals(result.content, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        mockMvc.get("/report").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { reportService.getReports() } returns result

        // then
        mockMvc.get("/report").andExpect {
          status { isUnauthorized() }
        }
      }
    }

    @Nested
    inner class GetReportTests
    {
      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { reportService.getReport(1) } returns report

        // then
        mockMvc.get("/report/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { reportService.getReport(1) } returns report

        // then
        mockMvc.get("/report/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore= TEST_EXECUTION)
      fun `gets report, given admin account`()
      {
        // when
        val expected = report.copy()

        every { reportService.getReport(1) } returns expected

        // then
        val actual = mockMvc.get("/report/1").andExpect {
          status { isOk() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("moderator", setupBefore= TEST_EXECUTION)
      fun `gets report, given moderator account`()
      {
        // when
        val expected = report.copy()

        every { reportService.getReport(1) } returns expected

        // then
        val actual = mockMvc.get("/report/1").andExpect {
          status { isOk() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore= TEST_EXECUTION)
      fun `gets report, given contributor account`()
      {
        // when
        val expected = report.copy()

        every { reportService.getReport(1) } returns expected

        // then
        val actual = mockMvc.get("/report/1").andExpect {
          status { isOk() }
        }.body<Report>()

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("viewer", setupBefore= TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { reportService.getReport(1) } returns report

        // then
        mockMvc.get("/report/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { reportService.getReport(1) } returns report

        // then
        mockMvc.get("/report/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 404 not found, when service throws NoSuchElementException`()
      {
        // when
        every { reportService.getReport(1) } throws NoSuchElementException()

        // then
        mockMvc.get("/report/1").andExpect {
          status { isNotFound() }
        }
      }
    }

    @Nested
    inner class DeleteReportTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `uses correct media type`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          content { EXPECTED_MEDIA_TYPE }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `receives path variable, when path variable provided`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          haveParameterValue("id", 1.toString())
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 200 ok, given admin account`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isOk() }
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given moderator account`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given contributor account`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `responds with 403 forbidden, given viewer account`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isForbidden() }
        }
      }

      @Test
      fun `responds with 401 unauthorized, given no account`()
      {
        // when
        every { reportService.deleteReport(1) } returns Unit

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isUnauthorized() }
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
      {
        // when
        every { reportService.deleteReport(1) } throws NoAuthorizationException()

        // then
        mockMvc.delete("/report/1").andExpect {
          status { isUnauthorized() }
        }
      }
    }
  }
}
