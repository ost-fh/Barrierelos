package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class Website(
  public var id: Long = 0,
  public var user: User,
  public var url: String,
  public var domain: String,
  public var category: CategoryEnum,
  @EncodeDefault
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  @EncodeDefault
  public var score: Double? = null,
  @EncodeDefault
  public var tags: MutableSet<WebsiteTag> = mutableSetOf(),
  @EncodeDefault
  public var deleted: Boolean = false,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
