package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class Webpage
(
  public var id: Long = 0,
  public var website: Website,
  public var user: User,
  public var displayUrl: String,
  public var url: String,
  @EncodeDefault
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  @EncodeDefault
  public var deleted: Boolean = false,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
