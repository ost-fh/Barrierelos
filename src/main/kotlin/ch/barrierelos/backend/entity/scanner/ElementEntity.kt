package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "element")
public class ElementEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var elementId: Long = 0,
  @ManyToOne
  @JoinColumn(name="check_element_fk", nullable=false)
  public var checkElement: CheckElementEntity,
  public var target: String,
  public var html: String,
  public var modified: Timestamp = Timestamp(0),
)
