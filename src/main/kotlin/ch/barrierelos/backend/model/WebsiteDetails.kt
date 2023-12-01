package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteDetails(
    public var id: Long = 0,
    public var score: Double,
    public var webpages: MutableSet<WebpageDetails>,
    public var modified: Long = System.currentTimeMillis(),
    public var created: Long = System.currentTimeMillis(),
)
