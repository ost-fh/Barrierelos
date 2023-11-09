package ch.barrierelos.backend.model.scanner

import ch.barrierelos.backend.enums.scanner.CheckTypeEnum
import ch.barrierelos.backend.enums.scanner.ImpactEnum
import kotlinx.serialization.Serializable

@Serializable
public data class Check
(
  public val id: Long,
  public val code: String,
  public val type: CheckTypeEnum,
  public val impact: ImpactEnum,
  public val testedCount: Int,
  public val passedCount: Int,
  public val violatedCount: Int,
  public val incompleteCount: Int,
  public val violatingElements: MutableSet<CheckElement>,
  public val incompleteElements: MutableSet<CheckElement>,
  public var modified: Long = System.currentTimeMillis(),
)
