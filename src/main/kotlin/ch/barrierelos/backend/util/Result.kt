package ch.barrierelos.backend.util

import ch.barrierelos.backend.constant.Header
import kotlinx.serialization.Serializable
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders

public fun <E, M> Page<E>.toResult(lastModified: Long, toModel: E.() -> M): Result<M>
{
  return Result(
    page =  number,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages,
    lastModified = lastModified,
    content = content.map { it.toModel() }
  )
}

public fun <E, M> Page<E>.toResult(toModel: E.() -> M): Result<M>
{
  return Result(
    page =  number,
    size = if(totalElements < size) totalElements.toInt() else size,
    totalElements = totalElements,
    totalPages = totalPages,
    content = content.map { it.toModel() }
  )
}

public fun <T> Result<T>.toHeaders(): HttpHeaders
{
  val headers = HttpHeaders()
  
  headers.set(Header.PAGINATION_PAGE, this.page.toString())
  headers.set(Header.PAGINATION_SIZE, this.size.toString())
  headers.set(Header.PAGINATION_TOTAL_ELEMENTS, this.totalElements.toString())
  headers.set(Header.PAGINATION_TOTAL_PAGES, this.totalPages.toString())
  headers.set(Header.PAGINATION_TOTAL_PAGES, this.totalPages.toString())
  headers.set(Header.STATUS_LAST_MODIFIED, this.lastModified.toString())

  headers.accessControlExposeHeaders = Header.ALL
  
  return headers
}

@Serializable
public data class Result<T>
(
  public var page: Int,
  public var size: Int,
  public var totalElements: Long,
  public var totalPages: Int,
  public var lastModified: Long = 0,
  public var content: List<T>,
)
{
  public constructor(content: List<T>, lastModified: Long = 0): this(0, content.size, content.size.toLong(), 1, lastModified, content)
}
