package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebpageEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebpageEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebpageEntity()

    // then
    val actual = createWebpageEntity()

    Assertions.assertEquals(expected.webpageId, actual.webpageId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.path, actual.path)
    Assertions.assertEquals(expected.url, actual.url)
    Assertions.assertEquals(expected.status, actual.status)
    Assertions.assertEquals(expected.deleted, actual.deleted)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
