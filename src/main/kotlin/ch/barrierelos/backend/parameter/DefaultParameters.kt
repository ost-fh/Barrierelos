package ch.barrierelos.backend.parameter

import ch.barrierelos.backend.enums.OrderEnum

public data class DefaultParameters
(
  public val page: Int? = null,
  public val size: Int? = null,
  public var sort: String? = null,
  public var order: OrderEnum? = null,
  public val modifiedAfter: Long? = null,
  public val createdAfter: Long? = null,
)
