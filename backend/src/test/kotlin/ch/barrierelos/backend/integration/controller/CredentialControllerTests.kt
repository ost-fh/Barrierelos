package ch.barrierelos.backend.integration.controller

import ch.barrierelos.backend.exception.InvalidCredentialsException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.service.CredentialService
import ch.barrierelos.backend.util.toJson
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.url.haveParameterValue
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.put

@Nested
abstract class CredentialControllerTests : ControllerTests()
{
  @MockkBean
  lateinit var credentialService: CredentialService

  val credential = createCredentialModel()

  @Nested
  @DisplayName("Update Credential")
  inner class UpdateCredentialTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `uses correct media type`()
    {
      // when
      every { credentialService.updateCredential(credential) } returns credential

      // then
      mockMvc.put("/credential/1").andExpect {
        content { EXPECTED_MEDIA_TYPE }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives credential, when provided in body`()
    {
      // when
      val credential = credential.copy().apply {
        id = 1
        userId = 1
        password = "password"
        issuer = "issuer"
        subject = "subject"
      }

      every { credentialService.updateCredential(credential) } returns credential

      // then
      mockMvc.put("/credential/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = credential.toJson()
      }.andExpect {
        status { isOk() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `receives path variable, when path variable provided`()
    {
      // when
      every { credentialService.updateCredential(credential) } returns credential

      // then
      mockMvc.put("/credential/1").andExpect {
        haveParameterValue("id", 1.toString())
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 200 ok, when updated, given account`()
    {
      // when
      val expected = credential.copy().apply {
        id = 1
        userId = 1
      }

      every { credentialService.updateCredential(expected) } returns expected

      println(expected.toJson())

      // then
      mockMvc.put("/credential/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = expected.toJson()
      }.andExpect {
        status { isOk() }
      }
    }

    @Test
    fun `responds with 401 unauthorized, given no account`()
    {
      // when
      every { credentialService.updateCredential(credential) } returns credential

      // then
      mockMvc.put("/credential/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = credential.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 400 bad request, when service throws InvalidCredentialsException`()
    {
      // when
      every { credentialService.updateCredential(credential.copy(userId = 1)) } throws InvalidCredentialsException("")

      // then
      mockMvc.put("/credential/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = credential.toJson()
      }.andExpect {
        status { isBadRequest() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 401 unauthorized, when service throws NoAuthorizationException`()
    {
      // when
      every { credentialService.updateCredential(credential.copy(userId = 1)) } throws NoAuthorizationException()

      // then
      mockMvc.put("/credential/1") {
        contentType = EXPECTED_MEDIA_TYPE
        content = credential.toJson()
      }.andExpect {
        status { isUnauthorized() }
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `responds with 415 unsupported media type, when no credential provided in body`()
    {
      // when
      every { credentialService.updateCredential(credential.copy(userId = 1)) } returns credential

      // then
      mockMvc.put("/credential/1").andExpect {
        status { isUnsupportedMediaType() }
      }
    }
  }
}
