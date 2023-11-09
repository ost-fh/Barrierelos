package ch.barrierelos.backend.message.scanner

import ch.barrierelos.backend.enums.scanner.CheckTypeEnum
import ch.barrierelos.backend.enums.scanner.ImpactEnum
import kotlinx.serialization.Serializable

@Serializable
public data class CheckMessage
(
  public val id: String,
  public val type: CheckTypeEnum,
  public val impact: ImpactEnum,
  public val testedCount: Int,
  public val passedCount: Int,
  public val violatedCount: Int,
  public val incompleteCount: Int,
  public val violatingElements: MutableSet<CheckElementMessage>,
  public val incompleteElements: MutableSet<CheckElementMessage>,
)
