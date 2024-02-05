package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.enums.StatusEnum
import ch.barrierelos.backend.exception.InvalidUrlException
import ch.barrierelos.backend.exception.NoAuthorizationException
import ch.barrierelos.backend.exception.ReferenceNotExistsException
import ch.barrierelos.backend.exception.UrlNotMatchingWebsiteDomainException
import ch.barrierelos.backend.message.WebpageMessage
import ch.barrierelos.backend.model.Webpage
import ch.barrierelos.backend.repository.WebpageRepository
import ch.barrierelos.backend.repository.WebpageRepository.Companion.findAll
import ch.barrierelos.backend.repository.WebsiteRepository
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.orThrow
import ch.barrierelos.backend.util.throwIfNoValidUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class WebpageService
{
  @Autowired
  private lateinit var websiteRepository: WebsiteRepository

  @Autowired
  private lateinit var webpageRepository: WebpageRepository

  @Autowired
  private lateinit var statisticService: StatisticService

  public fun addWebpage(webpageMessage: WebpageMessage): Webpage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val website = websiteRepository.findById(webpageMessage.websiteId)
      .orThrow(ReferenceNotExistsException("Website with this websiteId does not exist."))
      .toModel()

    throwIfNoValidUrl(webpageMessage.url)
    throwIfUrlNotMatchesWebsiteDomain(webpageMessage.url, website.domain)

    var displayUrl = "^https?://([^?]+)(/?\\?.*)?$".toRegex()
      .find(webpageMessage.url)?.groups?.get(1)?.value
      ?: throw throw InvalidUrlException(webpageMessage.url)

    if(displayUrl.endsWith('/'))
      displayUrl = displayUrl.dropLast(1)

    throwIfDisplayUrlAlreadyExists(displayUrl)

    var webpage = webpageMessage.toModel(website, displayUrl)
    webpage = this.webpageRepository.save(webpage.toEntity()).toModel()
    statisticService.addWebpageScan(webpage)

    return webpage
  }

  public fun updateWebpage(webpage: Webpage): Webpage
  {
    Security.assertAnyRolesOrId(webpage.user.id, RoleEnum.ADMIN, RoleEnum.MODERATOR)

    val existingWebpage = this.webpageRepository.findById(webpage.id).orElseThrow().toModel()

    if(Security.hasRole(RoleEnum.MODERATOR))
    {
      throwIfIllegallyModified(webpage, existingWebpage)
    }
    else if(Security.hasRole(RoleEnum.CONTRIBUTOR))
    {
      if(!Security.hasId(webpage.user.id))
      {
        throwIfDeleted(webpage)
      }

      throwIfStatusIllegallyChanged(webpage, existingWebpage)
      throwIfIllegallyModified(webpage, existingWebpage)
    }

    webpage.modified = System.currentTimeMillis()

    return this.webpageRepository.save(webpage.toEntity()).toModel()
  }

  public fun getWebpages(showDeleted: Boolean = false, showBlocked: Boolean = false, defaultParameters: DefaultParameters = DefaultParameters()): Result<Webpage>
  {
    return this.webpageRepository.findAll(showDeleted, showBlocked, defaultParameters)
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
    if((webpage.user != existingWebpage.user)
      || (webpage.displayUrl != existingWebpage.displayUrl)
      || (webpage.url != existingWebpage.url)
      || (webpage.created != existingWebpage.created)
      || (webpage.status != existingWebpage.status && (webpage.status == StatusEnum.PENDING_INITIAL || webpage.status == StatusEnum.PENDING_RESCAN || webpage.status == StatusEnum.READY))
    )
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

  private fun throwIfDisplayUrlAlreadyExists(displayUrl: String)
  {
    if(this.webpageRepository.existsByDisplayUrl(displayUrl))
    {
      throw ReferenceNotExistsException("Webpage with this displayUrl already exists.")
    }
  }

  private fun throwIfUrlNotMatchesWebsiteDomain(url: String, domain: String)
  {
    val secondAndTopLevelDomain = "^([^.]+\\.)*([^.]+\\.[^.]+)$".toRegex()
      .find(domain)?.groups?.get(2)?.value
      ?: throw InvalidUrlException(url)
    if(!url.matches("^https?://([^/]+\\.)?${secondAndTopLevelDomain}(/.*)?$".toRegex()))
    {
      throw UrlNotMatchingWebsiteDomainException("Url (${url}) does not match website domain (${domain}).")
    }
  }

  private fun throwIfNotExists(webpageId: Long)
  {
    if(!this.webpageRepository.existsById(webpageId))
    {
      throw NoSuchElementException("Webpage with this id does not exist.")
    }
  }
}
