package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.WebpageEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exceptions.AlreadyExistsException
import ch.barrierelos.backend.exceptions.InvalidPathException
import ch.barrierelos.backend.exceptions.InvalidUrlException
import ch.barrierelos.backend.exceptions.NoAuthorizationException
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.repository.WebpageRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageService
{
  @Autowired
  private lateinit var webpageRepository: WebpageRepository

  public fun addWebpage(webpage: Webpage): Webpage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    if(!Security.hasRole(RoleEnum.ADMIN))
    {
      Security.assertId(webpage.userId)
    }

    throwIfNoValidPath(webpage)
    throwIfUrlNotMatchesPath(webpage)
    throwIfPathAlreadyExists(webpage)

    val timestamp = System.currentTimeMillis()
    webpage.created = timestamp
    webpage.modified = timestamp
    webpage.status = StatusEnum.PENDING_INITIAL

    return this.webpageRepository.save(webpage.toEntity()).toModel()
  }

  public fun updateWebpage(webpage: Webpage): Webpage
  {
    Security.assertAnyRolesOrId(webpage.userId, RoleEnum.ADMIN, RoleEnum.MODERATOR)

    val existingWebpage = this.webpageRepository.findById(webpage.id).orElseThrow().toModel()

    if(Security.hasRole(RoleEnum.MODERATOR))
    {
      throwIfIllegallyModified(webpage, existingWebpage)
    }
    else if(Security.hasRole(RoleEnum.CONTRIBUTOR))
    {
      if(!Security.hasId(webpage.userId))
      {
        throwIfDeleted(webpage)
      }

      throwIfStatusIllegallyChanged(webpage, existingWebpage)
      throwIfIllegallyModified(webpage, existingWebpage)
    }

    webpage.modified = System.currentTimeMillis()

    return this.webpageRepository.save(webpage.toEntity()).toModel()
  }

  public fun getWebpages(defaultParameters: DefaultParameters = DefaultParameters()): Result<Webpage>
  {
    return this.webpageRepository.findAll(defaultParameters, WebpageEntity::class.java, WebpageEntity::toModel)
  }

  public fun getWebpage(webpageId: Long): Webpage
  {
    return this.webpageRepository.findById(webpageId).orElseThrow().toModel()
  }

  public fun deleteWebpage(webpageId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR)

    throwIfNotExists(webpageId)

    this.webpageRepository.deleteById(webpageId)
  }

  private fun throwIfDeleted(webpage: Webpage)
  {
    if(webpage.deleted)
    {
      throw NoAuthorizationException()
    }
  }

  private fun throwIfIllegallyModified(webpage: Webpage, existingWebpage: Webpage)
  {
    if((webpage.userId != existingWebpage.userId)
      || (webpage.path != existingWebpage.path)
      || (webpage.url != existingWebpage.url)
      || (webpage.created != existingWebpage.created)
      || (webpage.status != existingWebpage.status && (webpage.status == StatusEnum.PENDING_INITIAL || webpage.status == StatusEnum.PENDING_RESCAN || webpage.status == StatusEnum.READY)))
    {
      throw IllegalArgumentException("Webpage illegally modified.")
    }
  }

  private fun throwIfStatusIllegallyChanged(webpage: Webpage, existingWebpage: Webpage)
  {
    if(webpage.status != existingWebpage.status && webpage.status == StatusEnum.BLOCKED)
    {
      throw IllegalArgumentException("Webpage illegally modified.")
    }
  }

  private fun throwIfPathAlreadyExists(webpage: Webpage)
  {
    if(this.webpageRepository.existsByPathAndWebsiteFk(webpage.path, webpage.websiteId))
    {
      throw AlreadyExistsException("Webpage with that path already exists.")
    }
  }

  private fun throwIfNoValidPath(webpage: Webpage)
  {
    if(!webpage.path.matches("^(/[A-Za-z0-9-]+)+\$".toRegex()))
    {
      throw InvalidPathException("Path is not valid.")
    }
  }

  private fun throwIfUrlNotMatchesPath(webpage: Webpage)
  {
    if(!webpage.url.matches("^http://.*${webpage.path}.*\$".toRegex()) && !webpage.url.matches("^https://.*${webpage.path}.*\$".toRegex()))
    {
      throw InvalidUrlException("Path and url do not match.")
    }
  }

  private fun throwIfNotExists(webpageId: Long)
  {
    if(!this.webpageRepository.existsById(webpageId))
    {
      throw NoSuchElementException("Webpage with such id does not exist.")
    }
  }
}
