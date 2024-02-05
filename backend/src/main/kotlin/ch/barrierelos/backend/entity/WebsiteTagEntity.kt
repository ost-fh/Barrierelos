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
  public var websiteFk: Long? = null,
  public var userFk: Long? = null,
  @OneToOne
  @JoinColumn(name = "tag_fk", referencedColumnName = "tagId")
  public var tag: TagEntity,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
