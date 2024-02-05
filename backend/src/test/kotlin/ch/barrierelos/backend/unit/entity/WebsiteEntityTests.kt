package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebsiteEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteEntity()

    // then
    val actual = createWebsiteEntity()

    Assertions.assertEquals(expected.websiteId, actual.websiteId)
    Assertions.assertEquals(expected.user.userId, actual.user.userId)
    Assertions.assertEquals(expected.domain, actual.domain)
    Assertions.assertEquals(expected.url, actual.url)
    Assertions.assertEquals(expected.category, actual.category)
    Assertions.assertEquals(expected.status, actual.status)
    Assertions.assertEquals(expected.tags.size, actual.tags.size)
    Assertions.assertEquals(expected.deleted, actual.deleted)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
