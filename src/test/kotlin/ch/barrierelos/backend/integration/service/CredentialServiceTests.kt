package ch.barrierelos.backend.integration.service

import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.exception.InvalidCredentialsException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.helper.createCredentialModel
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.service.CredentialService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails

@Nested
abstract class CredentialServiceTests : ServiceTests()
{
  @Autowired
  lateinit var credentialService: CredentialService

  @Nested
  @DisplayName("Add Credential")
  inner class AddCredentialTests
  {
    @Test
    fun `adds credential, when valid credentials`()
    {
      // when
      val expected = createCredentialModel()

      // then
      val actual = credentialService.addCredential(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertNotEquals(expected.id, actual.id)
      Assertions.assertNotEquals(expected.password, actual.password)
      Assertions.assertEquals(expected.issuer, actual.issuer)
      Assertions.assertEquals(expected.subject, actual.subject)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertNotEquals(expected.created, actual.created)
    }

    @Test
    fun `cannot add credential, when no credentials`()
    {
      // when
      val credential = createCredentialModel()
      credential.password = null
      credential.issuer = null
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when invalid credentials`()
    {
      // when
      val credential = createCredentialModel()
      credential.password = ""
      credential.issuer = ""
      credential.subject = ""

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when password empty`()
    {
      // when
      val credential = createCredentialModel()
      credential.password = ""

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when password too short`()
    {
      // when
      val credential = createCredentialModel()
      credential.password = "12345"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when no password, issuer and subject`()
    {
      // when
      val credential = createCredentialModel()
      credential.password = null
      credential.issuer = null
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when no issuer`()
    {
      // when
      val credential = createCredentialModel()
      credential.issuer = null
      credential.subject = "subject"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when no subject`()
    {
      // when
      val credential = createCredentialModel()
      credential.issuer = "https://accounts.google.com"
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }

    @Test
    fun `cannot add credential, when wrong issuer`()
    {
      // when
      val credential = createCredentialModel()
      credential.issuer = "issuer"
      credential.subject = "subject"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.addCredential(credential)
      }
    }
  }

  @Nested
  @DisplayName("Update Credential")
  inner class UpdateCredentialTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `updates credential, given admin user`()
    {
      // when
      val expected = credentialService.addCredential(createCredentialModel().apply {
        userId = 10000
        password = "password2"
      })

      // then
      val actual = credentialService.updateCredential(expected.copy())

      Assertions.assertNotEquals(0, actual.id)
      Assertions.assertEquals(expected.id, actual.id)
      Assertions.assertNotEquals(expected.password, actual.password)
      Assertions.assertEquals(expected.issuer, actual.issuer)
      Assertions.assertEquals(expected.subject, actual.subject)
      Assertions.assertNotEquals(expected.modified, actual.modified)
      Assertions.assertEquals(expected.created, actual.created)
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when no credentials`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.password = null
      credential.issuer = null
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when invalid credentials`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.password = ""
      credential.issuer = ""
      credential.subject = ""

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when password empty`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.password = ""

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when password too short`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.password = "12345"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when no password, issuer and subject`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.password = null
      credential.issuer = null
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when no issuer`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.issuer = null
      credential.subject = "subject"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when no subject`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.issuer = "https://accounts.google.com"
      credential.subject = null

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update credential, when wrong issuer`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())
      credential.issuer = "issuer"
      credential.subject = "subject"

      // then
      Assertions.assertThrows(InvalidCredentialsException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    fun `cannot update user, given no account`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.updateCredential(credential)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot update user, given wrong account`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.updateCredential(credential)
      }
    }
  }

  @Nested
  @DisplayName("Get Credential")
  inner class GetCredentialTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets credential, given admin account`()
    {
      // when
      val expected = credentialService.addCredential(createCredentialModel().apply { userId = 5000 })

      // then
      val actual = credentialService.getCredential(expected.userId)

      Assertions.assertEquals(expected, actual)
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `gets credential, given correct account`()
    {
      val actual = credentialService.getCredential(Security.getUserId())

      Assertions.assertEquals(Security.getUserId(), actual.userId)
    }

    @Test
    fun `cannot get credential, given no account`()
    {
      // when
      val user = userRepository.findAll().first().toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.getCredential(user.id)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get credential, given wrong account`()
    {
      // when
      val user = userRepository.findAll().find { it.userId != Security.getUserId() }!!.toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.getCredential(user.id)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot get credential, when credential not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        credentialService.getCredential(5000000)
      }
    }
  }

  @Nested
  @DisplayName("Delete Credential")
  inner class DeleteCredentialTests
  {
    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes credential, given admin account`()
    {
      // when
      val credential = credentialService.addCredential(createCredentialModel())

      // then
      Assertions.assertDoesNotThrow {
        credentialService.deleteCredential(credential.userId)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        credentialService.getCredential(credential.userId)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `deletes credential, given correct account`()
    {
      // when
      val credential = credentialService.getCredential(Security.getUserId())

      // then
      Assertions.assertDoesNotThrow {
        credentialService.deleteCredential(credential.userId)
      }

      Assertions.assertThrows(NoSuchElementException::class.java) {
        credentialService.getCredential(credential.userId)
      }
    }

    @Test
    fun `cannot delete credential, given no account`()
    {
      // when
      val credential = credentialRepository.findAll().first().toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.deleteCredential(credential.userId)
      }
    }

    @Test
    @WithUserDetails("contributor", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete credential, given wrong account`()
    {
      // when
      val credential = credentialRepository.findAll().find { it.credentialId != Security.getUserId() }!!.toModel()

      // then
      Assertions.assertThrows(NoAuthorizationException::class.java) {
        credentialService.deleteCredential(credential.userId)
      }
    }

    @Test
    @WithUserDetails("admin", setupBefore= TestExecutionEvent.TEST_EXECUTION)
    fun `cannot delete credential, when credential not exists`()
    {
      Assertions.assertThrows(NoSuchElementException::class.java) {
        credentialService.deleteCredential(5000000)
      }
    }
  }
}
