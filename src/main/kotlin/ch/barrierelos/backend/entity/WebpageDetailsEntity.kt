package ch.barrierelos.backend.entity

import ch.barrierelos.backend.entity.scanner.WebpageResultEntity
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_details")
public class WebpageDetailsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var webpageDetailsId: Long = 0,
    public var websiteDetailsFk: Long,
    @OneToOne(fetch = FetchType.EAGER)
    public var webpage: WebpageEntity,
    @OneToOne(fetch = FetchType.EAGER)
    public var statistics: WebpageStatisticEntity? = null,
    @OneToOne(fetch = FetchType.EAGER)
    public var scanResult: WebpageResultEntity? = null,
    public var modified: Timestamp = Timestamp(0),
    public var created: Timestamp = Timestamp(0),
)
