package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.UserReport
import kotlinx.serialization.Serializable

@Serializable
public data class UserReportsMessage
(
  public val reports: Set<UserReport> = setOf(),
  public val messages: Set<ReportMessage> = setOf(),
  public val users: Set<User> = setOf(),
)
