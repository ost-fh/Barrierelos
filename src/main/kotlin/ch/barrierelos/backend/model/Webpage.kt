package ch.barrierelos.backend.model

import ch.barrierelos.backend.enums.CategoryEnum
import ch.barrierelos.backend.enums.StatusEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Webpage
(
  public var id: Long = 0,
  public var websiteFk: Long,
  public var userId: Long,
  public var domain: String,
  public var url: String,
  public var category: CategoryEnum,
  public var status: StatusEnum,
  public var modified: Long = System.currentTimeMillis(),
  public var created: Long = System.currentTimeMillis(),
)
