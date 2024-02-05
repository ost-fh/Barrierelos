package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebsiteTagEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteTagEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteTagEntity()

    // then
    val actual = createWebsiteTagEntity()

    Assertions.assertEquals(expected.websiteTagId, actual.websiteTagId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.userFk, actual.userFk)
    Assertions.assertEquals(expected.tag.tagId, actual.tag.tagId)
    Assertions.assertEquals(expected.tag.name, actual.tag.name)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
