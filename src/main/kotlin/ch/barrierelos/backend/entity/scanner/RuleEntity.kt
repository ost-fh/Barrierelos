package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "rule")
public class RuleEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var ruleId: Long = 0,
  @ManyToOne
  @JoinColumn(name="webpage_result_fk", nullable=false)
  public var webpageResult: WebpageResultEntity,
  public var code: String,
  @OneToMany(mappedBy = "rule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  public var checks: MutableSet<CheckEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(0),
)
