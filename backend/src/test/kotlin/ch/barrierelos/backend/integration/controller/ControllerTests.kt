package ch.barrierelos.backend.integration.controller

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.repository.CredentialRepository
import ch.barrierelos.backend.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import java.nio.charset.StandardCharsets.UTF_8

@AutoConfigureMockMvc
@SpringBootTest
abstract class ControllerTests
{
  companion object
  {
    val EXPECTED_MEDIA_TYPE = MediaType(APPLICATION_JSON, UTF_8)
  }

  @Autowired
  lateinit var mockMvc: MockMvc

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
}
