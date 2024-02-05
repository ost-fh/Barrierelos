package ch.barrierelos.backend.unit.entity

import ch.barrierelos.backend.helper.createUserEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class UserEntityTests
{
  @Test
  fun `is equals, when same content`()
  {
    // when
    val expected = createUserEntity()

    // then
    val actual = createUserEntity()

    Assertions.assertEquals(expected.userId, actual.userId)
    Assertions.assertEquals(expected.username, actual.username)
    Assertions.assertEquals(expected.firstname, actual.firstname)
    Assertions.assertEquals(expected.lastname, actual.lastname)
    Assertions.assertEquals(expected.email, actual.email)
    Assertions.assertEquals(expected.roles, actual.roles)
    Assertions.assertEquals(expected.deleted, actual.deleted)
    Assertions.assertEquals(expected.modified, actual.modified)
    Assertions.assertEquals(expected.created, actual.created)
  }
}
