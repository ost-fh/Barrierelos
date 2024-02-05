package ch.barrierelos.backend.message

import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.model.User
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.model.WebpageReport
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class WebpageReportsMessage
(
  @EncodeDefault
  public val reports: Set<WebpageReport> = setOf(),
  @EncodeDefault
  public val messages: Set<ReportMessage> = setOf(),
  @EncodeDefault
  public val users: Set<User> = setOf(),
  @EncodeDefault
  public val webpages: Set<Webpage> = setOf(),
)
