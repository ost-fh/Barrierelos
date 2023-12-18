package ch.barrierelos.backend.util

import ch.barrierelos.backend.constants.Header
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders

public fun <E, M> Page<E>.toResult(count:Long, lastModified: Long, toModel: E.() -> M): Result<M>
{
  return Result(
    page =  number,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages,
    count = count,
    lastModified = lastModified,
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
  headers.set(Header.STATUS_COUNT, this.count.toString())
  headers.set(Header.STATUS_LAST_MODIFIED, this.lastModified.toString())

  headers.accessControlExposeHeaders = Header.ALL
  
  return headers
}

public data class Result<T>
(
  public var page: Int,
  public var size: Int,
  public var totalElements: Long,
  public var totalPages: Int,
  public var count: Long,
  public var lastModified: Long,
  public var content: List<T>,
)
{
  public constructor(content: List<T>, count: Long, lastModified: Long): this(0, content.size, content.size.toLong(), 1, count, lastModified, content)
}
