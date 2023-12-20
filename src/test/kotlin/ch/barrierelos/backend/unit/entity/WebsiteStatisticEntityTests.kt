package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebsiteStatisticEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteStatisticEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteStatisticEntity()

    // then
    val actual = createWebsiteStatisticEntity()

    Assertions.assertEquals(expected.websiteStatisticId, actual.websiteStatisticId)
    Assertions.assertEquals(expected.score, actual.score)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
