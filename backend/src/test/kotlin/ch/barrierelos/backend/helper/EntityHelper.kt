package ch.barrierelos.backend.helper

import ch.barrierelos.backend.entity.*
import ch.barrierelos.backend.entity.scanner.*
import ch.barrierelos.backend.enums.*
import ch.barrierelos.backend.enums.scanner.CheckTypeEnum
import ch.barrierelos.backend.enums.scanner.ImpactEnum
import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
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

fun createScanJobEntity(websiteFk: Long = 0, userFk: Long = 0) = ScanJobEntity(
  websiteFk = websiteFk,
  userFk = userFk,
  modelVersion = "1.0.0",
  jobTimestamp = Timestamp(5000),
  domain = "example.com",
  webpages = mutableSetOf("https://example.com/test"),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebsiteResultEntity() = WebsiteResultEntity(
  modelVersion = "1.0.0",
  domain = "example.com",
  scanTimestamp = Timestamp(5000),
  scanStatus = ScanStatusEnum.SUCCESS,
  scanJob = createScanJobEntity(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebpageResultEntity() = WebpageResultEntity(
  websiteResult = createWebsiteResultEntity(),
  url = "https://example.com/test",
  scanStatus = ScanStatusEnum.SUCCESS,
  rules = mutableSetOf(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createRuleEntity(webpageResultEntity: WebpageResultEntity) = RuleEntity(
  webpageResult = webpageResultEntity,
  code = "aria-hidden-focus",
  description = "This is a test rule.",
  axeUrl = "https://dequeuniversity.com/rules/axe/4.2/aria-hidden-focus",
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createCheckEntity() = CheckEntity(
  rule = createRuleEntity(createWebpageResultEntity()),
  code = "aria-hidden-body",
  type = CheckTypeEnum.ALL,
  impact = ImpactEnum.MODERATE,
  testedCount = 4,
  passedCount = 1,
  violatedCount = 1,
  incompleteCount = 1,
  violatingElements = mutableSetOf(),
  incompleteElements = mutableSetOf(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createCheckElementEntity() = CheckElementEntity(
  target = "body",
  html = "<body></body>",
  issueDescription = "This is a test issue.",
  data = "",
  relatedElements = mutableSetOf(),
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createElementEntity() = ElementEntity(
  checkElement = createCheckElementEntity(),
  target = "body",
  html = "<body></body>",
  modified = Timestamp(5000),
  created = Timestamp(5000),
)
