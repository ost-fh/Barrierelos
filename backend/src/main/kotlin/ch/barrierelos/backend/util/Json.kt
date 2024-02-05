package ch.barrierelos.backend.util

import ch.barrierelos.backend.Application
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public val json: Json = Json {
  ignoreUnknownKeys = true
  encodeDefaults = true
}

public inline fun <reified T> T.toJson(): String
{
  return json.encodeToString(this)
}

public inline fun <reified T> String.fromJson(): T
{
  return json.decodeFromString<T>(this)
}

public fun readJsonResource(path: String): String
{
  return Application::class.java.getResource("/$path")?.readText() ?: ""
}

public inline fun <reified T> loadJsonResource(path: String): T
{
  return readJsonResource(path).fromJson<T>()
}
