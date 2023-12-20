package ch.barrierelos.backend.unit

import ch.barrierelos.backend.unit.converter.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Converter")
open class ConverterTestSuite
{
  @Nested
  @DisplayName("User Converter")
  inner class UserConverterTestSuite : UserConverterTests()

  @Nested
  @DisplayName("Credential Converter")
  inner class CredentialConverterTestSuite : CredentialConverterTests()

  @Nested
  @DisplayName("Tag Converter")
  inner class TagConverterTestSuite : TagConverterTests()

  @Nested
  @DisplayName("Website Tag Converter")
  inner class WebsiteTagConverterTestSuite : WebsiteTagConverterTests()

  @Nested
  @DisplayName("Website Converter")
  inner class WebsiteConverterTestSuite : WebsiteConverterTests()

  @Nested
  @DisplayName("Webpage Converter")
  inner class WebpageConverterTestSuite : WebpageConverterTests()

  @Nested
  @DisplayName("Website Statistic Converter")
  inner class WebsiteStatisticConverterTestSuite : WebsiteStatisticConverterTests()

  @Nested
  @DisplayName("Webpage Statistic Converter")
  inner class WebpageStatisticConverterTestSuite : WebpageStatisticConverterTests()

  @Nested
  @DisplayName("Website Scan Converter")
  inner class WebsiteScanConverterTestSuite : WebsiteScanConverterTests()

  @Nested
  @DisplayName("Webpage Scan Converter")
  inner class WebpageScanConverterTestSuite : WebpageScanConverterTests()

  @Nested
  @DisplayName("Report Converter")
  inner class ReportConverterTestSuite : ReportConverterTests()

  @Nested
  @DisplayName("Report Message Converter")
  inner class ReportMessageConverterTestSuite : ReportMessageConverterTests()

  @Nested
  @DisplayName("User Report Converter")
  inner class UserReportConverterTestSuite : UserReportConverterTests()

  @Nested
  @DisplayName("Website Report Converter")
  inner class WebsiteReportConverterTestSuite : WebsiteReportConverterTests()

  @Nested
  @DisplayName("Webpage Report Converter")
  inner class WebpageReportConverterTestSuite : WebpageReportConverterTests()
}
