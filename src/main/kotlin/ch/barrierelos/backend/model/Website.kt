package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Website(
  public var id: Long = 0,
  public var user: User,
  public var url: String,
  public var domain: String,
  public var category: CategoryEnum,
  public var status: StatusEnum = StatusEnum.PENDING_INITIAL,
  public var tags: MutableSet<WebsiteTag> = mutableSetOf(),
  public var deleted: Boolean = false,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
