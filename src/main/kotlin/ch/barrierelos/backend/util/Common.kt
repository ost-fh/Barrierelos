package ch.barrierelos.backend.util

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
