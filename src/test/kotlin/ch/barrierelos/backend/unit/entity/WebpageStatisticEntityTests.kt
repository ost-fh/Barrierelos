package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebpageStatisticEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageStatisticEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageStatisticEntity()

    // then
    val actual = createWebpageStatisticEntity()

    Assertions.assertEquals(expected.webpageStatisticId, actual.webpageStatisticId)
    Assertions.assertEquals(expected.score, actual.score)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
