package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebpageScanEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageScanEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageScanEntity()

    // then
    val actual = createWebpageScanEntity()

    Assertions.assertEquals(expected.webpageScanId, actual.webpageScanId)
    Assertions.assertEquals(expected.webpage.webpageId, actual.webpage.webpageId)
    Assertions.assertEquals(expected.webpageStatistic!!.webpageStatisticId, actual.webpageStatistic!!.webpageStatisticId)
    Assertions.assertEquals(expected.webpageResult, actual.webpageResult)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
