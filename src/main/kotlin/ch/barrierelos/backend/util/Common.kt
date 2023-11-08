package ch.barrierelos.backend.util

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
