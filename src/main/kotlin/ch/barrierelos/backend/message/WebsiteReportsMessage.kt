package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.WebsiteReport
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteReportsMessage
(
  public val reports: Set<WebsiteReport> = setOf(),
  public val messages: Set<ReportMessage> = setOf(),
  public val users: Set<User> = setOf(),
  public val websites: Set<Website> = setOf(),
)
