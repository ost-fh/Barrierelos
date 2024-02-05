package ch.barrierelos.backend.integration

import ch.barrierelos.backend.integration.controller.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Controller")
class ControllerTestSuite
{
  @Nested
  @DisplayName("User Controller")
  inner class UserControllerTestSuite : UserControllerTests()

  @Nested
  @DisplayName("Credential Controller")
  inner class CredentialControllerTestSuite : CredentialControllerTests()

  @Nested
  @DisplayName("Tag Controller")
  inner class TagControllerTestSuite : TagControllerTests()

  @Nested
  @DisplayName("Website Controller")
  inner class WebsiteControllerTestSuite : WebsiteControllerTests()

  @Nested
  @DisplayName("Webpage Controller")
  inner class WebpageControllerTestSuite : WebpageControllerTests()

  @Nested
  @DisplayName("Website Statistic Controller")
  inner class WebsiteStatisticControllerTestSuite : WebsiteStatisticControllerTests()

  @Nested
  @DisplayName("Webpage Statistic Controller")
  inner class WebpageStatisticControllerTestSuite : WebpageStatisticControllerTests()

  @Nested
  @DisplayName("Website Scan Controller")
  inner class WebsiteScanControllerTestSuite : WebsiteScanControllerTests()

  @Nested
  @DisplayName("Webpage Scan Controller")
  inner class WebpageScanControllerTestSuite : WebpageScanControllerTests()

  @Nested
  @DisplayName("Report Controller")
  inner class ReportControllerTestSuite : ReportControllerTests()

  @Nested
  @DisplayName("Report Message Controller")
  inner class ReportMessageControllerTestSuite : ReportMessageControllerTests()

  @Nested
  @DisplayName("User Report Controller")
  inner class UserReportControllerTestSuite : UserReportControllerTests()

  @Nested
  @DisplayName("Website Report Controller")
  inner class WebsiteReportControllerTestSuite : WebsiteReportControllerTests()

  @Nested
  @DisplayName("Webpage Report Controller")
  inner class WebpageReportControllerTestSuite : WebpageReportControllerTests()
}
