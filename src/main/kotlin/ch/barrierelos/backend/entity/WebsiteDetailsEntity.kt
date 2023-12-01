package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "website_details")
public class WebsiteDetailsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var websiteDetailsId: Long = 0,
    public var score: Double,
    @OneToMany(mappedBy = "website", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    public var webpages: MutableSet<WebpageDetailsEntity> = mutableSetOf(),
    public var modified: Timestamp = Timestamp(0),
    public var created: Timestamp = Timestamp(0),
)
