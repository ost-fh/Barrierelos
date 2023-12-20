package ch.barrierelos.backend.unit

import ch.barrierelos.backend.unit.model.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Model")
open class ModelTestSuite
{
  @Nested
  @DisplayName("User Model")
  inner class UserModelTestSuite : UserModelTests()

  @Nested
  @DisplayName("Credential Model")
  inner class CredentialModelTestSuite : CredentialModelTests()

  @Nested
  @DisplayName("Tag Model")
  inner class TagModelTestSuite : TagModelTests()

  @Nested
  @DisplayName("Website Tag Model")
  inner class WebsiteTagModelTestSuite : WebsiteTagModelTests()

  @Nested
  @DisplayName("Website Model")
  inner class WebsiteModelTestSuite : WebsiteModelTests()

  @Nested
  @DisplayName("Webpage Model")
  inner class WebpageModelTestSuite : WebpageModelTests()

  @Nested
  @DisplayName("Website Statistic Model")
  inner class WebsiteStatisticModelTestSuite : WebsiteStatisticModelTests()

  @Nested
  @DisplayName("Webpage Statistic Model")
  inner class WebpageStatisticModelTestSuite : WebpageStatisticModelTests()

  @Nested
  @DisplayName("Website Scan Model")
  inner class WebsiteScanModelTestSuite : WebsiteScanModelTests()

  @Nested
  @DisplayName("Webpage Scan Model")
  inner class WebpageScanModelTestSuite : WebpageScanModelTests()

  @Nested
  @DisplayName("Report Model")
  inner class ReportModelTestSuite : ReportModelTests()

  @Nested
  @DisplayName("Report Message Model")
  inner class ReportMessageModelTestSuite : ReportMessageModelTests()

  @Nested
  @DisplayName("User Report Model")
  inner class UserReportModelTestSuite : UserReportModelTests()

  @Nested
  @DisplayName("Website Report Model")
  inner class WebsiteReportModelTestSuite : WebsiteReportModelTests()

  @Nested
  @DisplayName("Webpage Report Model")
  inner class WebpageReportModelTestSuite : WebpageReportModelTests()
}
