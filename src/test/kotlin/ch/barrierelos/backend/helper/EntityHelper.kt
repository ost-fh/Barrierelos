package ch.barrierelos.backend.helper

import ch.barrierelos.backend.entity.*
import ch.barrierelos.backend.enums.*
import java.sql.Timestamp

fun createUserEntity() = UserEntity(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createCredentialEntity(userFk: Long = 0, issuer: String? = null, subject: String? = null) = CredentialEntity(
  userFk = userFk,
  password = "password",
  issuer = issuer,
  subject = subject,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createTagEntity() = TagEntity(
  name = "something"
)

fun createWebsiteTagEntity(userFk: Long = 0, websiteFk: Long = 0) = WebsiteTagEntity(
  websiteFk = websiteFk,
  userFk = userFk,
  tag = createTagEntity(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebsiteEntity(user: UserEntity = createUserEntity(), websiteFk: Long = 0) = WebsiteEntity(
  user = user,
  domain = "admin.ch",
  url = "https://admin.ch",
  category = CategoryEnum.GOVERNMENT_FEDERAL,
  status = StatusEnum.PENDING_INITIAL,
  tags = mutableSetOf(createWebsiteTagEntity(user.userId, websiteFk)),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebpageEntity(user: UserEntity = createUserEntity(), website: WebsiteEntity = createWebsiteEntity()) = WebpageEntity(
  website = website,
  user = user,
  displayUrl = "admin.ch/vbs/infos",
  url = "https://admin.ch/vbs/infos",
  status = StatusEnum.PENDING_INITIAL,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebsiteStatisticEntity() = WebsiteStatisticEntity(
  score = 80.0,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebpageStatisticEntity() = WebpageStatisticEntity(
  score = 80.0,
  weight = 1.0,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebsiteScanEntity(user: UserEntity = createUserEntity(), websiteFk: Long = 0) = WebsiteScanEntity(
  website = createWebsiteEntity(user, websiteFk),
  websiteStatistic = createWebsiteStatisticEntity(),
  websiteResult = null,
  webpageScans = mutableSetOf(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebpageScanEntity(user: UserEntity = createUserEntity(), website: WebsiteEntity = createWebsiteEntity()) = WebpageScanEntity(
  websiteScan = createWebsiteScanEntity(user, website.websiteId),
  webpage = createWebpageEntity(user, website),
  webpageStatistic = createWebpageStatisticEntity(),
  webpageResult = null,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createReportEntity(userFk: Long = 0) = ReportEntity(
  userFk = userFk,
  reason = ReasonEnum.INCORRECT,
  state = StateEnum.OPEN,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createReportMessageEntity(userFk: Long = 0, reportFk: Long = 0) = ReportMessageEntity(
  reportFk = reportFk,
  userFk = userFk,
  message = "Test message.",
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createUserReportEntity(userFk: Long = 0) = UserReportEntity(
  userFk = userFk,
  report = createReportEntity(userFk),
)

fun createWebsiteReportEntity(websiteFk: Long = 0) = WebsiteReportEntity(
  websiteFk = websiteFk,
  report = createReportEntity(websiteFk),
)

fun createWebpageReportEntity(webpageFk: Long = 0) = WebpageReportEntity(
  webpageFk = webpageFk,
  report = createReportEntity(webpageFk),
)
