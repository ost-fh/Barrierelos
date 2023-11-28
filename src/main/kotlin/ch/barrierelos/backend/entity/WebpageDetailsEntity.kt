package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "webpage_details")
public class WebpageDetailsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var webpageDetailsId: Long = 0,
    public var path: String,
    public var score: Double,
    @ManyToOne
    @JoinColumn(name="website_details_fk", nullable=false)
    public var website: WebsiteDetailsEntity,
    public var modified: Timestamp = Timestamp(0),
)
