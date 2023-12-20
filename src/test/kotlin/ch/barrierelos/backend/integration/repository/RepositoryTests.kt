package ch.barrierelos.backend.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
abstract class RepositoryTests
{
  @Autowired
  lateinit var entityManager: TestEntityManager
}
