package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Website
(
  public var id: Long = 0,
  public var userId: Long,
  public var domain: String,
  public var url: String,
  public var category: CategoryEnum,
  public var status: StatusEnum,
  public var tags: MutableSet<WebsiteTag> = mutableSetOf(),
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
