package ch.barrierelos.backend.unit

import ch.barrierelos.backend.helper.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ModelTests
{
  @Nested
  inner class UserTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createUserModel()

      // then
      val actual = createUserModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class CredentialTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createCredentialModel()

      // then
      val actual = createCredentialModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class TagTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createTagModel()

      // then
      val actual = createTagModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class WebsiteTagTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteTagModel()

      // then
      val actual = createWebsiteTagModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class WebsiteTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteModel()

      // then
      val actual = createWebsiteModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class WebpageTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebpageModel()

      // then
      val actual = createWebpageModel()

      assertEquals(expected, actual)
    }
  }

  @Nested
  inner class WebsiteStatisticTests
  {
    @Test
    fun `is equals, when same content`()
    {
      // when
      val expected = createWebsiteStatisticModel()

      // then
      val actual = createWebsiteStatisticModel()

      assertEquals(expected, actual)
    }
  }
}
