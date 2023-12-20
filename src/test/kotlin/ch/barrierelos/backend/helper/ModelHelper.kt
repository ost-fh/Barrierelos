package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.*
import ch.barrierelos.backend.model.*

fun createUserModel() = User(
  username = "username",
  firstname = "Firstname",
  lastname = "Lastname",
  email = "email@gmail.com",
  roles = mutableSetOf(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
  modified = 5000,
  created = 5000,
)

fun createCredentialModel(userId: Long = 0) = Credential(
  userId = userId,
  password = "password",
  issuer = "issuer",
  subject = "subject",
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
  modified = 5000,
  created = 5000,
)

fun createWebsiteScanModel(userId: Long = 0, websiteId: Long = 0, websiteStatisticId: Long = 0, websiteResultId: Long = 0) = WebsiteScan(
  websiteId = websiteId,
  websiteStatisticId = websiteStatisticId,
  websiteResultId = websiteResultId,
  userId = userId,
  modified = 5000,
  created = 5000,
)

fun createWebpageScanModel(userId: Long = 0, webpageId: Long = 0, webpageStatisticId: Long = 0, webpageResultId: Long = 0) = WebpageScan(
  webpageId = webpageId,
  webpageStatisticId = webpageStatisticId,
  webpageResultId = webpageResultId,
  userId = userId,
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
