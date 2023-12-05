package ch.barrierelos.backend.model

import ch.barrierelos.backend.model.scanner.WebsiteResult
import kotlinx.serialization.Serializable

@Serializable
public data class WebsiteDetails(
    public var id: Long = 0,
    public var website: Website,
    public var statistics: WebsiteStatistic? = null,
    public var scanResult: WebsiteResult? = null,
    public var webpages: MutableSet<WebpageDetails> = mutableSetOf(),
    public var user: User,
    public var modified: Long = System.currentTimeMillis(),
    public var created: Long = System.currentTimeMillis(),
)
