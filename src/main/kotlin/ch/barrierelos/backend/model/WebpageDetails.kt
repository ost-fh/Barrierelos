package ch.barrierelos.backend.model

import kotlinx.serialization.Serializable

@Serializable
public data class WebpageDetails(
    public var id: Long = 0,
    public var path: String,
    public var score: Double,
    public var modified: Long = System.currentTimeMillis(),
)
