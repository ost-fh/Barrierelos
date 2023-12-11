package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
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

fun createWebsiteModel(userId: Long = 0, websiteId: Long = 0) = Website(
  userId = userId,
  domain = "admin.ch",
  url = "https://admin.ch",
  category = CategoryEnum.GOVERNMENT_FEDERAL,
  status = StatusEnum.PENDING_INITIAL,
  tags = mutableSetOf(createWebsiteTagModel(userId, websiteId)),
  webpageCount = 0,
  modified = 5000,
  created = 5000,
)

fun createWebpageModel(userId: Long = 0, websiteId: Long = 0) = Webpage(
  websiteId = websiteId,
  userId = userId,
  path = "/vbs/infos",
  url = "https://admin.ch/vbs/infos",
  status = StatusEnum.PENDING_INITIAL,
  modified = 5000,
  created = 5000,
)
