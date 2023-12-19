package ch.barrierelos.backend.util

import ch.barrierelos.backend.exceptions.InvalidUrlException
import org.apache.commons.validator.routines.UrlValidator
import java.util.*

public fun <T> T?.throwIfNull(exception: Exception): T
{
  if(this == null)
  {
    throw exception
  }
  else
  {
    return this
  }
}

public fun <T> Optional<T>.orThrow(exception: Exception): T
{
  return this.orElseThrow { exception }
}

public fun throwIfNoValidUrl(url: String)
{
  val urlValidator = UrlValidator(arrayOf("http", "https"))
  if(!urlValidator.isValid(url)) throw InvalidUrlException(url)
}

public fun <T> MutableCollection<T>.clearAndAdd(element: T): Boolean
{
  this.clear()

  return this.add(element)
}

public fun <T> MutableCollection<T>.clearAndAddAll(elements: Collection<T>): Boolean
{
  this.clear()

  return this.addAll(elements)
}

public fun <T, K> Collection<T>.containsDuplicates(selector: (T) -> K): Boolean
{
  return this.size != this.distinctBy(selector).size
}
