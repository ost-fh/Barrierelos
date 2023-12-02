package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_tag")
public class WebsiteTagEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var websiteTagId: Long = 0,
  public var websiteFk: Long,
  public var userFk: Long,
  @OneToOne(cascade=[CascadeType.ALL], fetch=FetchType.EAGER)
  @JoinColumn(name = "tag_fk", referencedColumnName = "tagId")
  public var tag: TagEntity,
  public var modified: Timestamp = Timestamp(0),
  public var created: Timestamp = Timestamp(0),
)
