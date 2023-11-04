package ch.barrierelos.backend.util

public fun <T> T?.throwIfNotNull(exception: Exception): T
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
