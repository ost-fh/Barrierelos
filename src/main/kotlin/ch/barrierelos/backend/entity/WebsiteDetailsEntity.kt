package ch.barrierelos.backend.entity

import ch.barrierelos.backend.entity.scanner.WebsiteResultEntity
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_details")
public class WebsiteDetailsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var websiteDetailsId: Long = 0,
    @OneToOne(fetch = FetchType.EAGER)
    public var website: WebsiteEntity,
    @OneToOne(fetch = FetchType.EAGER)
    public var statistics: WebsiteStatisticEntity? = null,
    @OneToOne(fetch = FetchType.EAGER)
    public var scanResult: WebsiteResultEntity? = null,
    @OneToOne(fetch = FetchType.EAGER)
    public var user: UserEntity,
    public var modified: Timestamp = Timestamp(0),
    public var created: Timestamp = Timestamp(0),
)
