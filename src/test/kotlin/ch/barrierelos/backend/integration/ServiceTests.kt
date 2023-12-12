package ch.barrierelos.backend.integration

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.TagEntity
import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exceptions.*
import ch.barrierelos.backend.helper.*
import ch.barrierelos.backend.model.Tag
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.*
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.service.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
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
  lateinit var tagService: TagService

  @Autowired
  lateinit var websiteService: WebsiteService

  @Autowired
  lateinit var webpageService: WebpageService
  @Autowired
  lateinit var websiteStatisticService: WebsiteStatisticService

  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var credentialRepository: CredentialRepository

  @Autowired
  lateinit var tagRepository: TagRepository

  @Autowired
  lateinit var websiteTagRepository: WebsiteTagRepository

  @Autowired
  lateinit var websiteRepository: WebsiteRepository

  @Autowired
  lateinit var webpageRepository: WebpageRepository
  @Autowired
  lateinit var websiteStatisticRepository: WebsiteStatisticRepository

  @Autowired
  lateinit var websiteScanRepository: WebsiteScanRepository

  @Autowired
  lateinit var webpageScanRepository: WebpageScanRepository


  lateinit var admin: UserEntity
  lateinit var moderator: UserEntity
  lateinit var contributor: UserEntity
  lateinit var viewer: UserEntity

  @BeforeEach
  fun beforeEach()
  {
    // Add admin user
    val adminUser = createUserEntity()
    adminUser.username = "admin"
    adminUser.roles = mutableSetOf(RoleEnum.ADMIN)
    admin = userRepository.save(adminUser)

    // Add moderator user
    val moderatorUser = createUserEntity()
    moderatorUser.username = "moderator"
    moderatorUser.roles = mutableSetOf(RoleEnum.MODERATOR)
    moderator = userRepository.save(moderatorUser)

    // Add contributor user
    val contributorUser = createUserEntity()
    contributorUser.username = "contributor"
    contributorUser.roles = mutableSetOf(RoleEnum.CONTRIBUTOR)
    contributor = userRepository.save(contributorUser)

    // Add viewer user
    val viewerUser = createUserEntity()
    viewerUser.username = "viewer"
    viewerUser.roles = mutableSetOf(RoleEnum.VIEWER)
    viewer = userRepository.save(viewerUser)

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
    websiteTagRepository.deleteAll()
    tagRepository.deleteAll()
    webpageScanRepository.deleteAll()
    websiteScanRepository.deleteAll()
    webpageRepository.deleteAll()
    websiteRepository.deleteAll()
    userRepository.deleteAll()
    credentialRepository.deleteAll()
    webpageRepository.deleteAll()
    websiteTagRepository.deleteAll()
    websiteRepository.deleteAll()
    tagRepository.deleteAll()
    websiteStatisticRepository.deleteAll()
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
        assertThrows(AlreadyExistsException::class.java) {
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update user, when username already exists`()
      {
        // when
        val user = userService.addUser(createUserModel(), createCredentialModel())
        user.username = "admin"

        // then
        assertThrows(AlreadyExistsException::class.java) {
          userService.updateUser(user)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets users, given admin account`()
      {
        // when
        val users = userService.getUsers().content

        // then
        users.shouldNotBeEmpty()
        users.shouldHaveSize(4)
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets users with headers, when no parameters`()
      {
        // when
        val users = userService.getUsers()

        // then
        users.page.shouldBe(0)
        users.size.shouldBe(4)
        users.totalElements.shouldBe(4)
        users.totalPages.shouldBe(1)
        users.count.shouldBe(4)
        users.lastModified.shouldBeGreaterThan(0)
        users.content.shouldHaveSize(4)
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
        users.count.shouldBe(4)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets user, given admin account`()
      {
        // when
        val expected = userService.addUser(createUserModel(), createCredentialModel())

        // then
        val actual = userService.getUser(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `updates credential, given admin user`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel().apply {
          userId = 10000
          password = "password2"
          issuer = "issuer2"
          subject = "subject2"
        })

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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update credential, when no credentials`()
      {
        // when
        val credential = credentialService.addCredential(createCredentialModel())
        credential.password = null
        credential.issuer = null
        credential.subject = null

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.updateCredential(credential)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update credential, when invalid credentials`()
      {
        // when
        val credential = credentialService.addCredential(createCredentialModel())
        credential.password = ""
        credential.issuer = ""
        credential.subject = ""

        // then
        assertThrows(InvalidCredentialsException::class.java) {
          credentialService.updateCredential(credential)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets credential, given admin account`()
      {
        // when
        val expected = credentialService.addCredential(createCredentialModel().apply { userId = 5000 })

        // then
        val actual = credentialService.getCredential(expected.userId)

        assertEquals(expected, actual)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
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
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot delete credential, when credential not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          credentialService.deleteCredential(5000000)
        }
      }
    }
  }

  @Nested
  inner class TagServiceTests
  {
    @Nested
    inner class AddTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `adds tag, given admin account`()
      {
        // when
        val expected = createTagModel()

        // then
        val actual = tagService.addTag(expected.copy())

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.name, actual.name)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot add tag, given wrong account`()
      {
        // when
        val expected = createTagModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.addTag(expected)
        }
      }

      @Test
      fun `cannot add tag, given no account`()
      {
        // when
        val expected = createTagModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.addTag(expected)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot add tag, when name already exists`()
      {
        // when
        tagService.addTag(createTagModel())

        // then
        assertThrows(AlreadyExistsException::class.java) {
          tagService.addTag(createTagModel())
        }
      }
    }

    @Nested
    inner class UpdateTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `updates tag, given admin account`()
      {
        // when
        val expected = tagService.addTag(createTagModel())
        expected.name = "new"

        // then
        val actual = tagService.updateTag(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.name, actual.name)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot update tag, given wrong account`()
      {
        // when
        val expected = tagRepository.save(createTagEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.updateTag(expected)
        }
      }

      @Test
      fun `cannot update tag, given no account`()
      {
        // when
        val expected = tagRepository.save(createTagEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.updateTag(expected)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update tag, when name already exists`()
      {
        // given
        val existing = tagService.addTag(createTagModel())

        // when
        val expected = tagService.addTag(createTagModel().apply { name = "new" })
        expected.name = existing.name

        // then
        assertThrows(AlreadyExistsException::class.java) {
          tagService.updateTag(expected)
        }
      }
    }

    @Nested
    inner class GetTagsTests
    {
      @Test
      fun `gets tags`()
      {
        // given
        tagRepository.save(createTagEntity().apply { name = "one" })
        tagRepository.save(createTagEntity().apply { name = "two" })

        // when
        val tags = tagService.getTags()

        // then
        tags.shouldNotBeEmpty()
        tags.shouldHaveSize(2)
        assertTrue(tags.any { it.name == "one" })
        assertTrue(tags.any { it.name == "two" })
      }
    }

    @Nested
    inner class GetTagTests
    {
      @Test
      fun `gets tag`()
      {
        // when
        val expected = tagRepository.save(createTagEntity()).toModel()

        // then
        val actual = tagService.getTag(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      fun `cannot get tag, when tag not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          tagService.getTag(5000000)
        }
      }
    }

    @Nested
    inner class DeleteTagTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `deletes tag, given admin account`()
      {
        // when
        val tag = tagService.addTag(createTagModel())
        val tagsBefore = tagService.getTags()

        // then
        assertDoesNotThrow {
          tagService.deleteTag(tag.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          tagService.getTag(tag.id)
        }

        val tagsAfter = tagService.getTags()

        tagsBefore.shouldContain(tag)
        tagsAfter.shouldNotContain(tag)
        tagsBefore.shouldContainAll(tagsAfter)
        tagsAfter.shouldNotContainAll(tagsBefore)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot delete tag, given wrong account`()
      {
        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.deleteTag(1)
        }
      }

      @Test
      fun `cannot delete tag, given no account`()
      {
        // then
        assertThrows(NoAuthorizationException::class.java) {
          tagService.deleteTag(1)
        }
      }
    }
  }

  @Nested
  inner class WebsiteServiceTests
  {
    lateinit var tag: Tag

    @BeforeEach
    fun beforeEach()
    {
      tag = tagRepository.save(TagEntity(name = "test")).toModel()
    }

    fun createAndAddWebsite(user: UserEntity, domain: String, tag: Tag? = null): Website
    {
      val website = websiteRepository.save(createWebsiteEntity(user).also {
        it.domain = domain
        it.url = domain
        it.tags.clear()
      })

      if(tag != null)
      {
        val websiteTag = websiteTagRepository.save(createWebsiteTagEntity(admin.userId, website.websiteId).also { it.tag = tag.toEntity() })
        website.tags.add(websiteTag)
        websiteRepository.save(website)
      }

      return website.toModel()
    }


    @Nested
    inner class AddWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `adds website, given admin account`()
      {
        // when
        val websiteMessage = createWebsiteMessage()
        websiteMessage.tags = mutableSetOf(tag.name)
        val expected = createWebsiteModel()
        expected.tags.first().tag = tag
        val expectedTag = expected.tags.first()

        // then
        val actual = websiteService.addWebsite(websiteMessage)
        val actualTag = actual.tags.first()

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(admin.toModel(), actual.user)
        assertEquals(expected.domain, actual.domain)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.tags.size, actual.tags.size)
        assertEquals(expected.deleted, actual.deleted)

        actual.tags.shouldHaveSize(1)
        assertNotEquals(0, actualTag.id)
        assertNotEquals(expectedTag.id, actualTag.id)
        assertEquals(actual.id, actualTag.websiteId)
        assertEquals(admin.userId, actualTag.userId)
        assertEquals(expectedTag.tag.id, actualTag.tag.id)
        assertEquals(expectedTag.tag.name, actualTag.tag.name)
        assertEquals(expected.deleted, actual.deleted)
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot add website, given wrong account`()
      {
        // when
        val website = createWebsiteMessage()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.addWebsite(website)
        }
      }

      @Test
      fun `cannot add website, given no account`()
      {
        // when
        val website = createWebsiteMessage()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.addWebsite(website)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `adds website and ignore duplicate tags, given duplicate tags`()
      {
        // when
        val websiteMessage = createWebsiteMessage()
        websiteMessage.tags = mutableSetOf("test", "test")

        // then
        val website = websiteService.addWebsite(websiteMessage)
        assertEquals(1, website.tags.size)
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot add website, when domain already exists`()
      {
        // when
        val website = createWebsiteMessage()
        website.tags.clear()
        websiteService.addWebsite(website)

        // then
        assertThrows(AlreadyExistsException::class.java) {
          websiteService.addWebsite(website)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot add website, when invalid url`()
      {
        // when
        val website = createWebsiteMessage()
        website.tags.clear()
        website.url = "barrierefrei.ch/test"

        // then
        assertThrows(InvalidUrlException::class.java) {
          websiteService.addWebsite(website)
        }
      }
    }

    @Nested
    inner class UpdateWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `updates website, given admin account`()
      {
        // when
        val expected = websiteRepository.save(createWebsiteEntity(contributor).also { it.tags.clear() }).toModel()
        expected.domain = "admin2.ch"
        expected.url = "https://admin2.ch"
        expected.category = CategoryEnum.GOVERNMENT_CANTONAL
        expected.status = StatusEnum.PENDING_RESCAN
        expected.tags = mutableSetOf()

        // then
        val actual = websiteService.updateWebsite(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.domain, actual.domain)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.tags, actual.tags)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `updates website, given moderator account`()
      {
        // when
        val expected = websiteRepository.save(createWebsiteEntity(contributor).also { it.tags.clear() }).toModel()
        expected.status = StatusEnum.BLOCKED

        // then
        val actual = websiteService.updateWebsite(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.domain, actual.domain)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.tags, actual.tags)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `updates website, given correct contributor account`()
      {
        // when
        val expected = websiteRepository.save(createWebsiteEntity(contributor).also { it.tags.clear() }).toModel()
        expected.deleted = true

        // then
        val actual = websiteService.updateWebsite(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.domain, actual.domain)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.tags, actual.tags)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot update website status, given wrong contributor account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity(moderator).also { it.tags.clear() }).toModel()
        website.deleted = true

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot update website, given viewer account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity(contributor).also { it.tags.clear() }).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      fun `cannot update website, given no account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity(contributor).also { it.tags.clear() }).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `cannot update website, when illegally modified, given moderator account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity().also {
          it.user = contributor
          it.tags.clear()
          it.status = StatusEnum.READY
        }).toModel()
        website.domain = "newdomain.org"

        // then
        assertThrows(IllegalArgumentException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `cannot change status of website, when current status is pending, given moderator account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity().also {
          it.tags.clear()
          it.user = contributor
          it.status = StatusEnum.PENDING_INITIAL
        }).toModel()
        website.status = StatusEnum.READY

        // then
        assertThrows(IllegalArgumentException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `cannot update website, when tags illegally modified, given moderator account`()
      {
        // when
        val website = createAndAddWebsite(moderator, "barrierelos.ch", tag)
        website.tags.first().modified = 1000000

        // then
        assertThrows(IllegalArgumentException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot update website, when status changed, given contributor account`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity().also {
          it.tags.clear()
          it.user = contributor
          it.status = StatusEnum.BLOCKED
        }).toModel()
        website.status = StatusEnum.READY

        // then
        assertThrows(IllegalArgumentException::class.java) {
          websiteService.updateWebsite(website)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update website, when website not exists`()
      {
        // when
        val website = createWebsiteModel()

        // then
        assertThrows(NoSuchElementException::class.java) {
          websiteService.updateWebsite(website)
        }
      }
    }

    @Nested
    inner class GetWebsitesTests
    {
      @Test
      fun `gets websites`()
      {
        // given
        createAndAddWebsite(admin, "barrierelos.ch", tag)
        createAndAddWebsite(moderator, "barrierelos.org")

        // when
        val websites = websiteService.getWebsites().content

        // then
        websites.shouldNotBeEmpty()
        websites.shouldHaveSize(2)
        assertTrue(websites.any { it.domain == "barrierelos.ch" })
        assertTrue(websites.any { it.domain == "barrierelos.org" })
        assertTrue(websites.any { website -> website.tags.any { websiteTag -> websiteTag.tag.id == tag.id } })
        assertTrue(websites.any { it.tags.isEmpty() })
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets websites with headers, when no parameters`()
      {
        // given
        websiteRepository.save(createWebsiteEntity().also {
          it.domain = "barrierelos.ch"
          it.url = "barrierelos.ch"
          it.user = admin
          it.tags.clear()
        })
        websiteRepository.save(createWebsiteEntity().also {
          it.domain = "barrierelos.org"
          it.url = "barrierelos.org"
          it.user = admin
          it.tags.clear()
        })

        // when
        val websites = websiteService.getWebsites()

        // then
        websites.page.shouldBe(0)
        websites.size.shouldBe(2)
        websites.totalElements.shouldBe(2)
        websites.totalPages.shouldBe(1)
        websites.count.shouldBe(2)
        websites.lastModified.shouldBeGreaterThan(0)
        websites.content.shouldHaveSize(2)
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `gets websites with headers, when with parameters`()
      {
        // given
        websiteRepository.save(createWebsiteEntity().also {
          it.domain = "barrierelos.ch"
          it.url = "barrierelos.ch"
          it.user = admin
          it.tags.clear()
        })
        websiteRepository.save(createWebsiteEntity().also {
          it.domain = "barrierelos.org"
          it.url = "barrierelos.org"
          it.user = admin
          it.tags.clear()
        })

        // when
        val websites = websiteService.getWebsites(
          DefaultParameters(
            page = 1,
            size = 1,
            sort = "domain",
            order = OrderEnum.ASC
          )
        )

        // then
        websites.page.shouldBe(1)
        websites.size.shouldBe(1)
        websites.totalElements.shouldBe(2)
        websites.totalPages.shouldBe(2)
        websites.count.shouldBe(2)
        websites.lastModified.shouldBeGreaterThan(0)
        websites.content.shouldHaveSize(1)
      }
    }

    @Nested
    inner class GetWebsiteTests
    {
      @Test
      fun `gets website`()
      {
        // when
        val expected = websiteRepository.save(createWebsiteEntity().also {
          it.domain = "barrierelos.ch"
          it.url = "barrierelos.ch"
          it.user = contributor
          it.tags.clear()
        }).toModel()

        // then
        val actual = websiteService.getWebsite(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      fun `cannot get website, when website not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          websiteService.getWebsite(5000000)
        }
      }
    }

    @Nested
    inner class DeleteWebsiteTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `deletes website, given admin account`()
      {
        // when
        val website = createAndAddWebsite(admin, "barrierelos.ch", tag)
        val websitesBefore = websiteService.getWebsites().content

        // then
        assertDoesNotThrow {
          websiteService.deleteWebsite(website.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          websiteService.getWebsite(website.id)
        }

        websiteTagRepository.findById(website.tags.first().id).shouldBeEmpty()

        val websitesAfter = websiteService.getWebsites().content

        websitesBefore.shouldContain(website)
        websitesAfter.shouldNotContain(website)
        websitesBefore.shouldContainAll(websitesAfter)
        websitesAfter.shouldNotContainAll(websitesBefore)
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `deletes website, given moderator account`()
      {
        // when
        val website = createAndAddWebsite(admin, "barrierelos.ch", tag)
        val websitesBefore = websiteService.getWebsites().content

        // then
        assertDoesNotThrow {
          websiteService.deleteWebsite(website.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          websiteService.getWebsite(website.id)
        }

        websiteTagRepository.findById(website.tags.first().id).shouldBeEmpty()

        val websitesAfter = websiteService.getWebsites().content

        websitesBefore.shouldContain(website)
        websitesAfter.shouldNotContain(website)
        websitesBefore.shouldContainAll(websitesAfter)
        websitesAfter.shouldNotContainAll(websitesBefore)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot delete website, given contributor account`()
      {
        // when
        val website = createAndAddWebsite(contributor, "barrierelos.ch", tag)

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.deleteWebsite(website.id)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot delete website, given viewer account`()
      {
        // when
        val website = createAndAddWebsite(viewer, "barrierelos.ch", tag)

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.deleteWebsite(website.id)
        }
      }

      @Test
      fun `cannot delete website, given no account`()
      {
        // when
        val website = createAndAddWebsite(contributor, "barrierelos.ch", tag)

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteService.deleteWebsite(website.id)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot delete website, when website not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          websiteService.deleteWebsite(5000000)
        }
      }
    }
  }

  @Nested
  inner class WebpageServiceTests
  {
    private fun createAndAddWebsite(): Website
    {
      return websiteService.addWebsite(createWebsiteMessage().also {
        it.tags.clear()
      })
    }

    @Nested
    inner class AddWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `adds webpage, given admin account`()
      {
        // when
        val website = createAndAddWebsite()
        val webpageMessage = createWebpageMessage(website.id)
        val expected = createWebpageModel()

        // then
        val actual = webpageService.addWebpage(webpageMessage)

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(website.user, actual.user)
        assertEquals(expected.displayUrl, actual.displayUrl)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertNotEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot add webpage, given viewer account`()
      {
        // when
        val webpage = createWebpageMessage()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.addWebpage(webpage)
        }
      }

      @Test
      fun `cannot add webpage, given no account`()
      {
        // when
        val webpage = createWebpageMessage()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.addWebpage(webpage)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot add webpage, when path already exists`()
      {
        // when
        val website = createAndAddWebsite()
        val webpageMessage = createWebpageMessage(website.id)
        webpageService.addWebpage(webpageMessage)

        // then
        assertThrows(ReferenceNotExistsException::class.java) {
          webpageService.addWebpage(webpageMessage)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot add webpage, when invalid url`()
      {
        // when
        val website = createAndAddWebsite()
        val webpageMessage = createWebpageMessage(website.id)
        webpageMessage.url = "https://barrierelos/test"

        // then
        assertThrows(InvalidUrlException::class.java) {
          webpageService.addWebpage(webpageMessage)
        }
      }
    }

    @Nested
    inner class UpdateWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `updates webpage, given admin account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val expected = webpageRepository.save(createWebpageEntity(admin, website)).toModel()
        expected.url = "https://barrierelos.ch/test"
        expected.displayUrl = "barrierelos.ch/test"
        expected.status = StatusEnum.PENDING_RESCAN

        // then
        val actual = webpageService.updateWebpage(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.displayUrl, actual.displayUrl)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `updates webpage, given moderator account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val expected = webpageRepository.save(createWebpageEntity(admin, website)).toModel()
        expected.status = StatusEnum.BLOCKED

        // then
        val actual = webpageService.updateWebpage(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.displayUrl, actual.displayUrl)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `updates webpage, given correct contributor account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val expected = webpageRepository.save(createWebpageEntity(contributor, website)).toModel()
        expected.deleted = true

        // then
        val actual = webpageService.updateWebpage(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.url, actual.url)
        assertEquals(expected.displayUrl, actual.displayUrl)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.deleted, actual.deleted)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot update webpage status, given wrong contributor account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(moderator, website)).toModel()
        webpage.deleted = true

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot update webpage, given viewer account`()
      {
        // when
        val webpage = createWebpageEntity().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }

      @Test
      fun `cannot update webpage, given no account`()
      {
        // when
        val webpage = createWebpageEntity().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `cannot update webpage, when illegally modified, given moderator account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(admin, website).also {
          it.status = StatusEnum.READY
        }).toModel()
        webpage.displayUrl = "new.url"

        // then
        assertThrows(IllegalArgumentException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `cannot change status of webpage, when current status is pending, given moderator account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(contributor, website).also {
          it.status = StatusEnum.PENDING_INITIAL
        }).toModel()
        webpage.status = StatusEnum.READY

        // then
        assertThrows(IllegalArgumentException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot update webpage, when webpage not exists`()
      {
        // when
        val webpage = createWebpageModel()

        // then
        assertThrows(NoSuchElementException::class.java) {
          webpageService.updateWebpage(webpage)
        }
      }
    }

    @Nested
    inner class GetWebpagesTests
    {
      @Test
      fun `gets webpages`()
      {
        // given
        val website = websiteRepository.save(createWebsiteEntity(admin).also { it.tags.clear() })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/one"
          url = "https://barrierelos.ch/one"
        })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/two"
          url = "https://barrierelos.ch/two"
        })

        // when
        val webpages = webpageService.getWebpages().content

        // then
        webpages.shouldNotBeEmpty()
        webpages.shouldHaveSize(2)
        assertTrue(webpages.any { it.displayUrl == "barrierelos.ch/one" })
        assertTrue(webpages.any { it.displayUrl == "barrierelos.ch/two" })
      }

      @Test
      fun `gets webpages with headers, when no parameters`()
      {
        // given
        val website = websiteRepository.save(createWebsiteEntity(admin).also { it.tags.clear() })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/one"
          url = "https://barrierelos.ch/one"
        })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/two"
          url = "https://barrierelos.ch/two"
        })

        // when
        val webpages = webpageService.getWebpages()

        // then
        webpages.page.shouldBe(0)
        webpages.size.shouldBe(2)
        webpages.totalElements.shouldBe(2)
        webpages.totalPages.shouldBe(1)
        webpages.count.shouldBe(2)
        webpages.lastModified.shouldBeGreaterThan(0)
        webpages.content.shouldHaveSize(2)
      }

      @Test
      fun `gets webpages with headers, when with parameters`()
      {
        // given
        val website = websiteRepository.save(createWebsiteEntity(admin).also { it.tags.clear() })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/one"
          url = "https://barrierelos.ch/one"
        })
        webpageRepository.save(createWebpageEntity(contributor, website).apply {
          displayUrl = "barrierelos.ch/two"
          url = "https://barrierelos.ch/two"
        })

        // when
        val webpages = webpageService.getWebpages(
          DefaultParameters(
            page = 1,
            size = 1,
            sort = "url",
            order = OrderEnum.ASC
          )
        )

        // then
        webpages.page.shouldBe(1)
        webpages.size.shouldBe(1)
        webpages.totalElements.shouldBe(2)
        webpages.totalPages.shouldBe(2)
        webpages.count.shouldBe(2)
        webpages.lastModified.shouldBeGreaterThan(0)
        webpages.content.shouldHaveSize(1)
      }
    }

    @Nested
    inner class GetWebpageTests
    {
      @Test
      fun `gets webpage`()
      {
        // when
        val website = websiteRepository.save(createWebsiteEntity(admin).also { it.tags.clear() })
        val expected = webpageRepository.save(createWebpageEntity(admin, website)).toModel()

        // then
        val actual = webpageService.getWebpage(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      fun `cannot get webpage, when webpage not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          webpageService.getWebpage(5000000)
        }
      }
    }

    @Nested
    inner class DeleteWebpageTests
    {
      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `deletes webpage, given admin account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(admin, website)).toModel()
        val webpagesBefore = webpageService.getWebpages().content

        // then
        assertDoesNotThrow {
          webpageService.deleteWebpage(webpage.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          webpageService.getWebpage(webpage.id)
        }

        val webpagesAfter = webpageService.getWebpages().content

        webpagesBefore.shouldContain(webpage)
        webpagesAfter.shouldNotContain(webpage)
        webpagesBefore.shouldContainAll(webpagesAfter)
        webpagesAfter.shouldNotContainAll(webpagesBefore)
      }

      @Test
      @WithUserDetails("moderator", setupBefore = TEST_EXECUTION)
      fun `deletes webpage, given moderator account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(contributor, website)).toModel()
        val webpagesBefore = webpageService.getWebpages().content

        // then
        assertDoesNotThrow {
          webpageService.deleteWebpage(webpage.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          webpageService.getWebpage(webpage.id)
        }

        val webpagesAfter = webpageService.getWebpages().content

        webpagesBefore.shouldContain(webpage)
        webpagesAfter.shouldNotContain(webpage)
        webpagesBefore.shouldContainAll(webpagesAfter)
        webpagesAfter.shouldNotContainAll(webpagesBefore)
      }

      @Test
      @WithUserDetails("contributor", setupBefore = TEST_EXECUTION)
      fun `cannot delete webpage, given contributor account`()
      {
        // when
        val website = createAndAddWebsite().toEntity()
        val webpage = webpageRepository.save(createWebpageEntity(contributor, website)).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.deleteWebpage(webpage.id)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore = TEST_EXECUTION)
      fun `cannot delete webpage, given viewer account`()
      {
        // when
        val webpage = createWebpageEntity().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.deleteWebpage(webpage.id)
        }
      }

      @Test
      fun `cannot delete webpage, given no account`()
      {
        // when
        val webpage = createWebpageEntity().toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          webpageService.deleteWebpage(webpage.id)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore = TEST_EXECUTION)
      fun `cannot delete webpage, when webpage not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          webpageService.deleteWebpage(5000000)
        }
      }
    }
  }

  @Nested
  inner class WebsiteStatisticServiceTests
  {
    @Nested
    inner class AddWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `adds website statistic, given admin account`()
      {
        // when
        val expected = createWebsiteStatisticModel()

        // then
        val actual = websiteStatisticService.addWebsiteStatistic(expected.copy())

        assertNotEquals(0, actual.id)
        assertNotEquals(expected.id, actual.id)
        assertEquals(expected.score, actual.score)
        assertNotEquals(expected.modified, actual.modified)
        assertNotEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `cannot add website statistic, given moderator account`()
      {
        // when
        val websiteStatistic = createWebsiteStatisticModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.addWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot add website statistic, given contributor account`()
      {
        // when
        val websiteStatistic = createWebsiteStatisticModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.addWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `cannot add website statistic, given viewer account`()
      {
        // when
        val websiteStatistic = createWebsiteStatisticModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.addWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      fun `cannot add websiteStatistic, given no account`()
      {
        // when
        val websiteStatistic = createWebsiteStatisticModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.addWebsiteStatistic(websiteStatistic)
        }
      }
    }

    @Nested
    inner class UpdateWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `updates website statistic, given admin account`()
      {
        // when
        val expected = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()
        expected.score = 20.0

        // then
        val actual = websiteStatisticService.updateWebsiteStatistic(expected.copy())

        assertNotEquals(0, actual.id)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.score, actual.score)
        assertNotEquals(expected.modified, actual.modified)
        assertEquals(expected.created, actual.created)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `cannot update website statistic, given moderator account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot update website statistic, given contributor account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `cannot update website statistic, given viewer account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      fun `cannot update website statistic, given no account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
        }
      }

      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `cannot update website statistic, when website statistic not exists`()
      {
        // when
        val websiteStatistic = createWebsiteStatisticModel()

        // then
        assertThrows(NoSuchElementException::class.java) {
          websiteStatisticService.updateWebsiteStatistic(websiteStatistic)
        }
      }
    }

    @Nested
    inner class GetWebsiteStatisticsTests
    {
      @Test
      fun `gets website statistics`()
      {
        // given
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 40.0
        })
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 60.0
        })

        // when
        val websiteStatistics = websiteStatisticService.getWebsiteStatistics().content

        // then
        websiteStatistics.shouldNotBeEmpty()
        websiteStatistics.shouldHaveSize(2)
        assertTrue(websiteStatistics.any { it.score == 40.0 })
        assertTrue(websiteStatistics.any { it.score == 60.0 })
      }

      @Test
      fun `gets website statistics with headers, when no parameters`()
      {
        // given
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 40.0
        })
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 60.0
        })

        // when
        val websiteStatistics = websiteStatisticService.getWebsiteStatistics()

        // then
        websiteStatistics.page.shouldBe(0)
        websiteStatistics.size.shouldBe(2)
        websiteStatistics.totalElements.shouldBe(2)
        websiteStatistics.totalPages.shouldBe(1)
        websiteStatistics.count.shouldBe(2)
        websiteStatistics.lastModified.shouldBeGreaterThan(0)
        websiteStatistics.content.shouldHaveSize(2)
      }

      @Test
      fun `gets website statistics with headers, when with parameters`()
      {
        // given
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 40.0
        })
        websiteStatisticRepository.save(createWebsiteStatisticEntity().apply {
          score = 60.0
        })

        // when
        val websiteStatistics = websiteStatisticService.getWebsiteStatistics(DefaultParameters(
          page = 1,
          size = 1,
          sort = "score",
          order = OrderEnum.ASC
        ))

        // then
        websiteStatistics.page.shouldBe(1)
        websiteStatistics.size.shouldBe(1)
        websiteStatistics.totalElements.shouldBe(2)
        websiteStatistics.totalPages.shouldBe(2)
        websiteStatistics.count.shouldBe(2)
        websiteStatistics.lastModified.shouldBeGreaterThan(0)
        websiteStatistics.content.shouldHaveSize(1)
      }
    }

    @Nested
    inner class GetWebsiteStatisticTests
    {
      @Test
      fun `gets website statistic`()
      {
        // when
        val expected = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        val actual = websiteStatisticService.getWebsiteStatistic(expected.id)

        assertEquals(expected, actual)
      }

      @Test
      fun `cannot get website statistic, when website statistic not exists`()
      {
        assertThrows(NoSuchElementException::class.java) {
          websiteStatisticService.getWebsiteStatistic(5000000)
        }
      }
    }

    @Nested
    inner class DeleteWebsiteStatisticTests
    {
      @Test
      @WithUserDetails("admin", setupBefore=TEST_EXECUTION)
      fun `deletes website statistic, given admin account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()
        val websiteStatisticsBefore = websiteStatisticService.getWebsiteStatistics().content

        // then
        assertDoesNotThrow {
          websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
        }

        assertThrows(NoSuchElementException::class.java) {
          websiteStatisticService.getWebsiteStatistic(websiteStatistic.id)
        }

        val websiteStatisticsAfter = websiteStatisticService.getWebsiteStatistics().content

        websiteStatisticsBefore.shouldContain(websiteStatistic)
        websiteStatisticsAfter.shouldNotContain(websiteStatistic)
        websiteStatisticsBefore.shouldContainAll(websiteStatisticsAfter)
        websiteStatisticsAfter.shouldNotContainAll(websiteStatisticsBefore)
      }

      @Test
      @WithUserDetails("moderator", setupBefore=TEST_EXECUTION)
      fun `cannot delete website statistic, given moderator account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
        }
      }

      @Test
      @WithUserDetails("contributor", setupBefore=TEST_EXECUTION)
      fun `cannot delete website statistic, given contributor account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
        }
      }

      @Test
      @WithUserDetails("viewer", setupBefore=TEST_EXECUTION)
      fun `cannot delete website statistic, given viewer account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
        }
      }

      @Test
      fun `cannot delete websiteStatistic, given no account`()
      {
        // when
        val websiteStatistic = websiteStatisticRepository.save(createWebsiteStatisticEntity()).toModel()

        // then
        assertThrows(NoAuthorizationException::class.java) {
          websiteStatisticService.deleteWebsiteStatistic(websiteStatistic.id)
        }
      }
    }
  }
}
