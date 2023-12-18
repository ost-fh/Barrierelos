package ch.barrierelos.backend.helper

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.message.WebpageMessage
import ch.barrierelos.backend.message.WebsiteMessage

fun createWebsiteMessage() = WebsiteMessage(
  url = "https://admin.ch",
  category = CategoryEnum.GOVERNMENT_FEDERAL,
  tags = mutableSetOf("something"),
)

fun createWebpageMessage(websiteId: Long = 0) = WebpageMessage(
  websiteId = websiteId,
  url = "https://admin.ch/vbs/infos"
)
