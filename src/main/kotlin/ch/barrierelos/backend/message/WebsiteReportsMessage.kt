package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.Website
import ch.barrierelos.backend.model.WebsiteReport
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class WebsiteReportsMessage
(
  @EncodeDefault
  public val reports: Set<WebsiteReport> = setOf(),
  @EncodeDefault
  public val messages: Set<ReportMessage> = setOf(),
  @EncodeDefault
  public val users: Set<User> = setOf(),
  @EncodeDefault
  public val websites: Set<Website> = setOf(),
)
