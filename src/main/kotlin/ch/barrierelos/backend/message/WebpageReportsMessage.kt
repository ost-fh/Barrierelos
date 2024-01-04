package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.model.WebpageReport
import kotlinx.serialization.Serializable

@Serializable
public data class WebpageReportsMessage
(
  public val reports: Set<WebpageReport> = setOf(),
  public val messages: Set<ReportMessage> = setOf(),
  public val users: Set<User> = setOf(),
  public val webpages: Set<Webpage> = setOf(),
)
