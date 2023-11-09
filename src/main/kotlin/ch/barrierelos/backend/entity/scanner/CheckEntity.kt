package ch.barrierelos.backend.entity.scanner

import ch.barrierelos.backend.enums.scanner.CheckTypeEnum
import ch.barrierelos.backend.enums.scanner.ImpactEnum
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "check")
public class CheckEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var checkId: Long = 0,
  @ManyToOne
  @JoinColumn(name="rule_fk", nullable=false)
  public var rule: RuleEntity,
  public var code: String,
  @Enumerated(EnumType.STRING)
  public var type: CheckTypeEnum,
  @Enumerated(EnumType.STRING)
  public var impact: ImpactEnum,
  public var testedCount: Int,
  public var passedCount: Int,
  public var violatedCount: Int,
  public var incompleteCount: Int,
  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinTable(
    name = "check_violating_element",
    joinColumns = [JoinColumn(name = "checkFk")],
    inverseJoinColumns = [JoinColumn(name = "checkElementFk")]
  )
  public var violatingElements: MutableSet<CheckElementEntity> = mutableSetOf(),
  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinTable(
    name = "check_incomplete_element",
    joinColumns = [JoinColumn(name = "checkFk")],
    inverseJoinColumns = [JoinColumn(name = "checkElementFk")]
  )
  public var incompleteElements: MutableSet<CheckElementEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(0),
)
