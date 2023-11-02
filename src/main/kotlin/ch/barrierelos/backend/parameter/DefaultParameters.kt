package ch.barrierelos.backend.parameter

import ch.barrierelos.backend.message.enums.Order

public data class DefaultParameters
(
  public val page: Int?,
  public val size: Int?,
  public val sort: String?,
  public val order: Order?,
  public val modifiedAfter: Long?
)
