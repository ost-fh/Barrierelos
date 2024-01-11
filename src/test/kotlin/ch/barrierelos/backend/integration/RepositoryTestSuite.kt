package ch.barrierelos.backend.integration

import ch.barrierelos.backend.integration.repository.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Repository")
class RepositoryTestSuite
{
  @Nested
  @DisplayName("User Repository")
  inner class UserRepositoryTestSuite : UserRepositoryTests()

  @Nested
  @DisplayName("Credential Repository")
  inner class CredentialRepositoryTestSuite : CredentialRepositoryTests()

  @Nested
  @DisplayName("Tag Repository")
  inner class TagRepositoryTestSuite : TagRepositoryTests()

  @Nested
  @DisplayName("Website Tag Repository")
  inner class WebsiteTagRepositoryTestSuite : WebsiteTagRepositoryTests()

  @Nested
  @DisplayName("Website Repository")
  inner class WebsiteRepositoryTestSuite : WebsiteRepositoryTests()

  @Nested
  @DisplayName("Webpage Repository")
  inner class WebpageRepositoryTestSuite : WebpageRepositoryTests()

  @Nested
  @DisplayName("Report Repository")
  inner class ReportRepositoryTestSuite : ReportRepositoryTests()

  @Nested
  @DisplayName("Report Message Repository")
  inner class ReportMessageRepositoryTestSuite : ReportMessageRepositoryTests()

  @Nested
  @DisplayName("User Report Repository")
  inner class UserReportRepositoryTestSuite : UserReportRepositoryTests()

  @Nested
  @DisplayName("Website Report Repository")
  inner class WebsiteReportRepositoryTestSuite : WebsiteReportRepositoryTests()

  @Nested
  @DisplayName("Webpage Report Repository")
  inner class WebpageReportRepositoryTestSuite : WebpageReportRepositoryTests()
}
