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
    Assertions.assertEquals(expected.website.websiteId, actual.website.websiteId)
    Assertions.assertEquals(expected.websiteStatistic!!.websiteStatisticId, actual.websiteStatistic!!.websiteStatisticId)
    Assertions.assertEquals(expected.websiteResult, actual.websiteResult)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
