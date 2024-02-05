package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.*
import ch.barrierelos.backend.enums.scanner.CheckTypeEnum
import ch.barrierelos.backend.enums.scanner.ImpactEnum
import ch.barrierelos.backend.enums.scanner.ScanStatusEnum
import ch.barrierelos.backend.model.*
import ch.barrierelos.backend.model.scanner.*
import kotlinx.datetime.Instant

fun createUserModel() = User(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = 5000,
  created = 5000,
)

fun createCredentialModel(userId: Long = 0, issuer: String? = null, subject: String? = null) = Credential(
  userId = userId,
  password = "password",
  issuer = issuer,
  subject = subject,
  modified = 5000,
  created = 5000,
)

fun createTagModel() = Tag(
  name = "something",
)

fun createWebsiteTagModel(userId: Long = 0, websiteId: Long = 0) = WebsiteTag(
  websiteId = websiteId,
  userId = userId,
  tag = createTagModel(),
  modified = 5000,
  created = 5000,
)

fun createWebsiteModel(user: User = createUserModel(), websiteId: Long = 0) = Website(
  user = user,
  domain = "admin.ch",
  url = "https://admin.ch",
  category = CategoryEnum.GOVERNMENT_FEDERAL,
  status = StatusEnum.PENDING_INITIAL,
  tags = mutableSetOf(createWebsiteTagModel(user.id, websiteId)),
  modified = 5000,
  created = 5000,
)

fun createWebpageModel(user: User = createUserModel(), website: Website = createWebsiteModel()) = Webpage(
  website = website,
  user = user,
  displayUrl = "admin.ch/vbs/infos",
  url = "https://admin.ch/vbs/infos",
  status = StatusEnum.PENDING_INITIAL,
  modified = 5000,
  created = 5000,
)

fun createWebsiteStatisticModel() = WebsiteStatistic(
  score = 80.0,
  modified = 5000,
  created = 5000,
)

fun createWebpageStatisticModel() = WebpageStatistic(
  score = 80.0,
  weight = 1.0,
  modified = 5000,
  created = 5000,
)

fun createWebsiteScanModel(user: User = createUserModel(), websiteId: Long = 0) = WebsiteScan(
  website = createWebsiteModel(user, websiteId),
  websiteStatistic = createWebsiteStatisticModel(),
  websiteResult = null,
  webpageScans = mutableSetOf(),
  modified = 5000,
  created = 5000,
)

fun createWebpageScanModel(user: User = createUserModel(), website: Website = createWebsiteModel()) = WebpageScan(
  webpage = createWebpageModel(user, website),
  webpageStatistic = createWebpageStatisticModel(),
  webpageResult = null,
  modified = 5000,
  created = 5000,
)

fun createReportModel(userId: Long = 0) = Report(
  userId = userId,
  reason = ReasonEnum.INCORRECT,
  state = StateEnum.OPEN,
  modified = 5000,
  created = 5000,
)

fun createReportMessageModel(userId: Long = 0, reportId: Long = 0) = ReportMessage(
  reportId = reportId,
  userId = userId,
  message = "Test message.",
  modified = 5000,
  created = 5000,
)

fun createUserReportModel(userId: Long = 0) = UserReport(
  userId = userId,
  report = createReportModel(userId),
)

fun createWebsiteReportModel(websiteId: Long = 0) = WebsiteReport(
  websiteId = websiteId,
  report = createReportModel(websiteId),
)

fun createWebpageReportModel(webpageId: Long = 0) = WebpageReport(
  webpageId = webpageId,
  report = createReportModel(webpageId),
)

fun createScanJobModel(websiteId: Long = 0, userId: Long = 0) = ScanJob(
  websiteId = websiteId,
  userId = userId,
  modelVersion = "1.0.0",
  jobTimestamp = Instant.fromEpochSeconds(5),
  domain = "example.com",
  webpages = mutableSetOf("https://example.com/test"),
  modified = 5000,
  created = 5000,
)

fun createWebsiteResultModel(id: Long = 0) = WebsiteResult(
  id = id,
  modelVersion = "1.0.0",
  domain = "example.com",
  scanTimestamp = Instant.fromEpochSeconds(5),
  scanStatus = ScanStatusEnum.SUCCESS,
  scanJob = createScanJobModel(),
  modified = 5000,
  created = 5000,
)

fun createWebpageResultModel(id: Long = 0) = WebpageResult(
  id = id,
  url = "https://example.com/test",
  scanStatus = ScanStatusEnum.SUCCESS,
  rules = mutableSetOf(),
  modified = 5000,
  created = 5000,
)

fun createRuleModel(id: Long = 0) = Rule(
  id = id,
  code = "aria-hidden-focus",
  description = "This is a test rule.",
  axeUrl = "https://dequeuniversity.com/rules/axe/4.2/aria-hidden-focus",
  wcagReferences = null,
  checks = mutableSetOf(),
  modified = 5000,
  created = 5000,
)

fun createCheckModel(id: Long = 0) = Check(
  id = id,
  code = "aria-hidden-body",
  type = CheckTypeEnum.ALL,
  impact = ImpactEnum.MODERATE,
  testedCount = 4,
  passedCount = 1,
  violatedCount = 1,
  incompleteCount = 1,
  violatingElements = mutableSetOf(),
  incompleteElements = mutableSetOf(),
  modified = 5000,
  created = 5000,
)

fun createCheckElementModel(id: Long = 0) = CheckElement(
  id = id,
  target = "body",
  html = "<body></body>",
  issueDescription = "This is a test issue.",
  data = "",
  relatedElements = mutableSetOf(),
  modified = 5000,
  created = 5000,
)

fun createElementModel(id: Long = 0) = Element(
  id = id,
  target = "body",
  html = "<body></body>",
  modified = 5000,
  created = 5000,
)
