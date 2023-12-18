package ch.barrierelos.backend.helper

import ch.barrierelos.backend.entity.*
import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
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

fun createCredentialEntity(userFk: Long = 0) = CredentialEntity(
  userFk = userFk,
  password = "password",
  issuer = "issuer",
  subject = "subject",
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
  modified = Timestamp(5000),
  created = Timestamp(5000),
)

fun createWebsiteScanEntity(userFk: Long = 0, websiteFk: Long = 0, websiteStatisticFk: Long = 0, websiteResultFk: Long = 0) = WebsiteScanEntity(
  websiteFk = websiteFk,
  websiteStatisticFk = websiteStatisticFk,
  websiteResultFk = websiteResultFk,
  userFk = userFk,
  modified = Timestamp(5000),
  created = Timestamp(5000),
)
