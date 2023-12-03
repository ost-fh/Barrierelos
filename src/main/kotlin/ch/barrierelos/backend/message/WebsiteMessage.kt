package ch.barrierelos.backend.message

public data class WebsiteMessage
(
  public var locale: String?,
  public var website: String,
  public var webpages: MutableSet<String>,
)
