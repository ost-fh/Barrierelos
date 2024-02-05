package ch.barrierelos.backend.message

import ch.barrierelos.backend.enums.CategoryEnum
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteMessage
(
  public var url: String,
  public var category: CategoryEnum,
  public var tags: MutableSet<String> = mutableSetOf(),
)
