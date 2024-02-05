package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*

@Entity
@Table(name = "check_violating_element")
public class CheckViolatingElementEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var checkViolatingElementId: Long? = null,
  public var checkFk: Long,
  public var checkElementFk: Long,
)
