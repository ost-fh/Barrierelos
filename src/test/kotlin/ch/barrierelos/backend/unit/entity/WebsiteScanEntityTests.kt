package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebsiteScanEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteScanEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteScanEntity()

    // then
    val actual = createWebsiteScanEntity()

    Assertions.assertEquals(expected.websiteScanId, actual.websiteScanId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.websiteStatisticFk, actual.websiteStatisticFk)
    Assertions.assertEquals(expected.websiteResultFk, actual.websiteResultFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
