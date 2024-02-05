package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.helper.createCredentialEntity
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.repository.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExtendWith(MockKExtension::class)
abstract class ServiceTests
{
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
  lateinit var webpageStatisticRepository: WebpageStatisticRepository
  @Autowired
  lateinit var websiteScanRepository: WebsiteScanRepository
  @Autowired
  lateinit var webpageScanRepository: WebpageScanRepository
  @Autowired
  lateinit var reportRepository: ReportRepository
  @Autowired
  lateinit var reportMessageRepository: ReportMessageRepository
  @Autowired
  lateinit var userReportRepository: UserReportRepository
  @Autowired
  lateinit var websiteReportRepository: WebsiteReportRepository
  @Autowired
  lateinit var webpageReportRepository: WebpageReportRepository

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
    webpageRepository.deleteAll()
    websiteTagRepository.deleteAll()
    websiteRepository.deleteAll()
    tagRepository.deleteAll()
    websiteStatisticRepository.deleteAll()
    webpageStatisticRepository.deleteAll()
    websiteScanRepository.deleteAll()
    webpageScanRepository.deleteAll()
    userReportRepository.deleteAll()
    websiteReportRepository.deleteAll()
    webpageReportRepository.deleteAll()
    reportRepository.deleteAll()
    reportMessageRepository.deleteAll()
  }
}
