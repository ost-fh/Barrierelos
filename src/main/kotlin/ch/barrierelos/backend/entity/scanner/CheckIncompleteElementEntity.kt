package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*

@Embeddable
@Table(name = "check_incomplete_element")
public class CheckIncompleteElementEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var checkIncompleteElementId: Long? = null,
  public var checkFk: Long,
  public var checkElementFk: Long,
)
