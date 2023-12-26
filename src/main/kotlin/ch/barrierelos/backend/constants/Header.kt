package ch.barrierelos.backend.constants

public object Header
{
  public const val PAGINATION_PAGE: String = "X-Pagination-Page"
  public const val PAGINATION_SIZE: String = "X-Pagination-Size"
  public const val PAGINATION_TOTAL_ELEMENTS: String = "X-Pagination-Total-Elements"
  public const val PAGINATION_TOTAL_PAGES: String = "X-Pagination-Total-Pages"

  public const val STATUS_LAST_MODIFIED: String = "X-Status-Last-Modified"

  public val ALL: List<String> = listOf(PAGINATION_PAGE, PAGINATION_SIZE, PAGINATION_TOTAL_ELEMENTS, PAGINATION_TOTAL_PAGES, STATUS_LAST_MODIFIED)
}
