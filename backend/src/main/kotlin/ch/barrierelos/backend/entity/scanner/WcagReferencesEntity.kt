package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*

@Entity
@Table(name = "wcag_references")
public class WcagReferencesEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var wcagReferencesId: Long = 0,
  @OneToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "rule_fk", nullable = false)
  public var rule: RuleEntity,
  public val version: String,
  public val level: String,
  @Suppress("JpaAttributeTypeInspection", "RedundantSuppression")
  public val criteria: MutableSet<String>,
)
