package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createWebsiteReportEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class WebsiteReportEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createWebsiteReportEntity()

    // then
    val actual = createWebsiteReportEntity()

    Assertions.assertEquals(expected.websiteReportId, actual.websiteReportId)
    Assertions.assertEquals(expected.websiteFk, actual.websiteFk)
    Assertions.assertEquals(expected.report.reportId, actual.report.reportId)
    Assertions.assertEquals(expected.report.userFk, actual.report.userFk)
    Assertions.assertEquals(expected.report.reason, actual.report.reason)
    Assertions.assertEquals(expected.report.state, actual.report.state)
    Assertions.assertEquals(expected.report.modified, actual.report.modified)
    Assertions.assertEquals(expected.report.created, actual.report.created)
  }
}
