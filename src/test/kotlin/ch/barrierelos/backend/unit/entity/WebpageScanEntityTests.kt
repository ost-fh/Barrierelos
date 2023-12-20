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
    Assertions.assertEquals(expected.webpageFk, actual.webpageFk)
    Assertions.assertEquals(expected.webpageStatisticFk, actual.webpageStatisticFk)
    Assertions.assertEquals(expected.webpageResultFk, actual.webpageResultFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
