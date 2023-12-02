package ch.barrierelos.backend.repository

import ch.barrierelos.backend.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository

public interface TagRepository : JpaRepository<TagEntity, Long>
{
  public fun findByName(name: String): TagEntity?
}
