package ch.barrierelos.backend.parameter

import ch.barrierelos.backend.message.enums.OrderEnum

public data class DefaultParameters
(
  public val page: Int? = null,
  public val size: Int? = null,
  public val sort: String? = null,
  public val order: OrderEnum? = null,
  public val modifiedAfter: Long? = null
)
