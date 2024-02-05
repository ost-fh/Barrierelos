package ch.barrierelos.backend.unit

import ch.barrierelos.backend.unit.entity.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Entity")
open class EntityTestSuite
{
  @Nested
  @DisplayName("User Entity")
  inner class UserEntityTestSuite : UserEntityTests()

  @Nested
  @DisplayName("Credential Entity")
  inner class CredentialEntityTestSuite : CredentialEntityTests()

  @Nested
  @DisplayName("Tag Entity")
  inner class TagEntityTestSuite : TagEntityTests()

  @Nested
  @DisplayName("Website Tag Entity")
  inner class WebsiteTagEntityTestSuite : WebsiteTagEntityTests()

  @Nested
  @DisplayName("Website Entity")
  inner class WebsiteEntityTestSuite : WebsiteEntityTests()

  @Nested
  @DisplayName("Webpage Entity")
  inner class WebpageEntityTestSuite : WebpageEntityTests()

  @Nested
  @DisplayName("Website Statistic Entity")
  inner class WebsiteStatisticEntityTestSuite : WebsiteStatisticEntityTests()

  @Nested
  @DisplayName("Webpage Statistic Entity")
  inner class WebpageStatisticEntityTestSuite : WebpageStatisticEntityTests()

  @Nested
  @DisplayName("Website Scan Entity")
  inner class WebsiteScanEntityTestSuite : WebsiteScanEntityTests()

  @Nested
  @DisplayName("Webpage Scan Entity")
  inner class WebpageScanEntityTestSuite : WebpageScanEntityTests()

  @Nested
  @DisplayName("Report Entity")
  inner class ReportEntityTestSuite : ReportEntityTests()

  @Nested
  @DisplayName("Report Message Entity")
  inner class ReportMessageEntityTestSuite : ReportMessageEntityTests()

  @Nested
  @DisplayName("User Report Entity")
  inner class UserReportEntityTestSuite : UserReportEntityTests()

  @Nested
  @DisplayName("Website Report Entity")
  inner class WebsiteReportEntityTestSuite : WebsiteReportEntityTests()

  @Nested
  @DisplayName("Webpage Report Entity")
  inner class WebpageReportEntityTestSuite : WebpageReportEntityTests()
}
