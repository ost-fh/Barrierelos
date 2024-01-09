package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.UserReport
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class UserReportsMessage
(
  @EncodeDefault
  public val reports: Set<UserReport> = setOf(),
  @EncodeDefault
  public val messages: Set<ReportMessage> = setOf(),
  @EncodeDefault
  public val users: Set<User> = setOf(),
)
