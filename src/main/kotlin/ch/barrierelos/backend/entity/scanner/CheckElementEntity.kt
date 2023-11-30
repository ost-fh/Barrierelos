package ch.barrierelos.backend.entity.scanner

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "check_element")
public class CheckElementEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var checkElementId: Long = 0,
  public var target: String,
  public var html: String,
  public var issueDescription: String,
  public var data: String,
  @OneToMany(mappedBy = "checkElement", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  public var relatedElements: MutableSet<ElementEntity> = mutableSetOf(),
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
