package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.converter.toModels
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.exception.AlreadyExistsException
import ch.barrierelos.backend.model.Tag
import ch.barrierelos.backend.repository.TagRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.orThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class TagService
{
  @Autowired
  private lateinit var tagRepository: TagRepository

  public fun addTag(tag: Tag): Tag
  {
    Security.assertRole(RoleEnum.ADMIN)

    throwIfNameAlreadyExists(tag)

    return this.tagRepository.save(tag.toEntity()).toModel(tag)
  }

  public fun updateTag(tag: Tag): Tag
  {
    Security.assertRole(RoleEnum.ADMIN)

    throwIfNameChangedAndAlreadyExists(tag)

    return this.tagRepository.save(tag.toEntity()).toModel()
  }

  public fun getTags(): Set<Tag>
  {
    return this.tagRepository.findAll().toModels()
  }

  public fun getTag(tagId: Long): Tag
  {
    return this.tagRepository.findById(tagId).orThrow(NoSuchElementException()).toModel()
  }

  public fun deleteTag(tagId: Long)
  {
    Security.assertRole(RoleEnum.ADMIN)

    this.tagRepository.deleteById(tagId)
  }

  private fun throwIfNameAlreadyExists(tag: Tag)
  {
    if(this.tagRepository.findByName(tag.name) != null)
    {
      throw AlreadyExistsException("Tag with that name already exists.")
    }
  }

  private fun throwIfNameChangedAndAlreadyExists(tag: Tag)
  {
    val existingTag = this.tagRepository.findById(tag.id).orThrow(NoSuchElementException()).toModel()

    if(tag.name != existingTag.name)
    {
      throwIfNameAlreadyExists(tag)
    }
  }
}
