package ch.barrierelos.backend.unit.converter

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createUserModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
abstract class UserConverterTests
{
  @Test
  fun `converts to model, when entity`()
  {
    // when
    val entity = createUserEntity()

    // then
    val model = createUserModel()

    Assertions.assertEquals(model, entity.toModel())
  }

  @Test
  fun `converts to entity, when model`()
  {
    // when
    val model = createUserModel()

    // then
    val expected = createUserEntity()
    val actual = model.toEntity()

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
