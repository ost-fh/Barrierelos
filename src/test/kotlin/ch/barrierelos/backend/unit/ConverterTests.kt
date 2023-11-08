package ch.barrierelos.backend.unit

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.helper.createUserEntity
import ch.barrierelos.backend.helper.createUserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConverterTests
{
  @Nested
  inner class UserTests
  {
    @Test
    fun `converts to model, when entity`()
    {
      // when
      val entity = createUserEntity()

      // then
      val model = createUserModel()

      assertEquals(model, entity.toModel())
    }

    @Test
    fun `converts to entity, when model`()
    {
      // when
      val model = createUserModel()

      // then
      val expectedEntity = createUserEntity()

      val actualEntity = model.toEntity()

      assertEquals(expectedEntity.userId, actualEntity.userId)
      assertEquals(expectedEntity.username, actualEntity.username)
      assertEquals(expectedEntity.firstname, actualEntity.firstname)
      assertEquals(expectedEntity.lastname, actualEntity.lastname)
      assertEquals(expectedEntity.email, actualEntity.email)
      assertEquals(expectedEntity.password, actualEntity.password)
      assertEquals(expectedEntity.issuer, actualEntity.issuer)
      assertEquals(expectedEntity.subject, actualEntity.subject)
      assertEquals(expectedEntity.roles, actualEntity.roles)
      assertEquals(expectedEntity.modified, actualEntity.modified)
    }
  }
}
