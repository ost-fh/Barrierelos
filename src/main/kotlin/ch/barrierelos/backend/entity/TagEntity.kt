package ch.barrierelos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "tag")
public class TagEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var tagId: Long = 0,
  public var name: String,
)
