package ch.barrierelos.backend.constant

public object Credential
{
  public const val MIN_LENGTH_USERNAME: Int = 3
  public const val MIN_LENGTH_PASSWORD: Int = 6

  public val ALLOWED_ISSUER: Set<String> = setOf(
    "https://accounts.google.com",
  )
}
