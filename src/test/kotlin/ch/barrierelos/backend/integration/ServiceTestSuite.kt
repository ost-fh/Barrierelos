package ch.barrierelos.backend.integration

import ch.barrierelos.backend.integration.service.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Service")
class ServiceTestSuite
{
  @Nested
  @DisplayName("User Service")
  inner class UserServiceTestSuite : UserServiceTests()

  @Nested
  @DisplayName("Credential Service")
  inner class CredentialServiceTestSuite : CredentialServiceTests()

  @Nested
  @DisplayName("Tag Service")
  inner class TagServiceTestSuite : TagServiceTests()

  @Nested
  @DisplayName("Website Service")
  inner class WebsiteServiceTestSuite : WebsiteServiceTests()

  @Nested
  @DisplayName("Webpage Service")
  inner class WebpageServiceTestSuite : WebpageServiceTests()

  @Nested
  @DisplayName("Website Statistic Service")
  inner class WebsiteStatisticServiceTestSuite : WebsiteStatisticServiceTests()

  @Nested
  @DisplayName("Webpage Statistic Service")
  inner class WebpageStatisticServiceTestSuite : WebpageStatisticServiceTests()

  @Nested
  @DisplayName("Website Scan Service")
  inner class WebsiteScanServiceTestSuite : WebsiteScanServiceTests()

  @Nested
  @DisplayName("Webpage Scan Service")
  inner class WebpageScanServiceTestSuite : WebpageScanServiceTests()

  @Nested
  @DisplayName("Report Service")
  inner class ReportServiceTestSuite : ReportServiceTests()

  @Nested
  @DisplayName("Report Message Service")
  inner class ReportMessageServiceTestSuite : ReportMessageServiceTests()

  @Nested
  @DisplayName("User Report Service")
  inner class UserReportServiceTestSuite : UserReportServiceTests()

  @Nested
  @DisplayName("Website Report Service")
  inner class WebsiteReportServiceTestSuite : WebsiteReportServiceTests()

  @Nested
  @DisplayName("Webpage Report Service")
  inner class WebpageReportServiceTestSuite : WebpageReportServiceTests()
}
