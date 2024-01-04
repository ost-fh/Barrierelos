package ch.barrierelos.backend.repository

import ch.barrierelos.backend.constant.Paging
import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.util.Result
import ch.barrierelos.backend.util.toResult
import jakarta.persistence.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.sql.Timestamp
import kotlin.math.max
import kotlin.math.min

@NoRepositoryBean
public interface Repository<E> : JpaRepository<E, Long>
{
  public fun findAllByCreatedAfterAndModifiedAfter(created: Timestamp, modified: Timestamp, pageable: Pageable): Page<E>

  public fun findAllByCreatedAfterAndModifiedAfter(created: Timestamp, modified: Timestamp, sort: Sort): List<E>

  public fun findAllByCreatedAfter(created: Timestamp, pageable: Pageable): Page<E>

  public fun findAllByCreatedAfter(created: Timestamp, sort: Sort): List<E>

  public fun findAllByModifiedAfter(modified: Timestamp, pageable: Pageable): Page<E>

  public fun findAllByModifiedAfter(modified: Timestamp, sort: Sort): List<E>

  @Query("SELECT MAX(e.modified) FROM #{#entityName} e")
  public fun lastModifiedTimestamp(): Timestamp?

  public companion object
  {
    public fun <E> Repository<E>.lastModified(): Long = this.lastModifiedTimestamp()?.time ?: 0

    @Deprecated(
      message="Use throwIfNotExists instead.",
      replaceWith = ReplaceWith(
        expression = "this.throwIfNotExists(id)",
        imports = ["ch.barrierelos.backend.repository.Repository.throwIfNotExists"]
      ),
    )
    public fun <E> Repository<E>.checkIfExists(id: Long)
    {
      if(!this.existsById(id)) throw NoSuchElementException()
    }

    public fun <E> Repository<E>.throwIfNotExists(id: Long)
    {
      if(!this.existsById(id)) throw NoSuchElementException()
    }

    private fun <E> Repository<E>.findAllByCreatedAfterAndModifiedAfter(created: Long, modified: Long, pageable: Pageable): Page<E> = findAllByCreatedAfterAndModifiedAfter(Timestamp(created), Timestamp(modified), pageable)

    private fun <E> Repository<E>.findAllByCreatedAfterAndModifiedAfter(created: Long, modified: Long, sort: Sort): List<E> = findAllByCreatedAfterAndModifiedAfter(Timestamp(created), Timestamp(modified), sort)

    private fun <E> Repository<E>.findAllByCreatedAfter(created: Long, pageable: Pageable): Page<E> = findAllByCreatedAfter(Timestamp(created), pageable)

    private fun <E> Repository<E>.findAllByCreatedAfter(created: Long, sort: Sort): List<E> = findAllByCreatedAfter(Timestamp(created), sort)

    private fun <E> Repository<E>.findAllByModifiedAfter(modified: Long, pageable: Pageable): Page<E> = findAllByModifiedAfter(Timestamp(modified), pageable)

    private fun <E> Repository<E>.findAllByModifiedAfter(modified: Long, sort: Sort): List<E> = findAllByModifiedAfter(Timestamp(modified), sort)

    public fun <E, M> Repository<E>.findAll(defaultParameters: DefaultParameters, entity: Class<E>, toModel: E.() -> M): Result<M>
    {
      val lastModified = this.lastModified()

      val page = defaultParameters.page
      val size = defaultParameters.size
      val createdAfter = defaultParameters.createdAfter
      val modifiedAfter = defaultParameters.modifiedAfter

      return if(page != null && size != null && size > 0)
      {
        val pageable = defaultParameters.toPageable(entity)

        if(createdAfter != null && modifiedAfter != null)
        {
          this.findAllByCreatedAfterAndModifiedAfter(createdAfter, modifiedAfter, pageable).toResult(lastModified, toModel)
        }
        else if(createdAfter != null)
        {
          this.findAllByCreatedAfter(createdAfter, pageable).toResult(lastModified, toModel)
        }
        else if(modifiedAfter != null)
        {
          this.findAllByModifiedAfter(modifiedAfter, pageable).toResult(lastModified, toModel)
        }
        else
        {
          this.findAll(pageable).toResult(lastModified, toModel)
        }
      }
      else
      {
        val sorting = defaultParameters.toSorting(entity)

        if(createdAfter != null && modifiedAfter != null)
        {
          Result(this.findAllByCreatedAfterAndModifiedAfter(createdAfter, modifiedAfter, sorting).map { it.toModel() }, lastModified)
        }
        else if(createdAfter != null)
        {
          Result(this.findAllByCreatedAfter(createdAfter, sorting).map { it.toModel() }, lastModified)
        }
        else if(modifiedAfter != null)
        {
          Result(this.findAllByModifiedAfter(modifiedAfter, sorting).map { it.toModel() }, lastModified)
        }
        else
        {
          Result(this.findAll(sorting).map { it.toModel() }, lastModified)
        }
      }
    }

    public fun <E> DefaultParameters.toSorting(entity: Class<E>): Sort
    {
      var sortStr = (if (sort == "id") null else sort) ?: (entity.declaredFields.find { it.isAnnotationPresent(Id::class.java) })?.name

      val fields = entity.declaredFields.map { it.name }

      if(sortStr != null && !fields.contains(sortStr))
      {
        if (sortStr.startsWith("is"))
        {
          sortStr = sortStr.substringAfter("is").replaceFirstChar { it.lowercase() }
        }
        else if(sortStr.endsWith("Id"))
        {
          sortStr = sortStr.substringBeforeLast("Id") + "Fk"
        }
      }

      var sorting = if(sortStr != null) Sort.by(sortStr) else Sort.unsorted()

      sorting = when(order)
      {
        OrderEnum.ASC -> sorting.ascending()
        OrderEnum.DESC -> sorting.descending()
        else -> sorting
      }

      return sorting
    }

    public fun <E> DefaultParameters.toPageable(entity: Class<E>): Pageable
    {
      val page = this.page ?: 0
      var size = this.size ?: Paging.DEFAULT_PAGE_SIZE
      size = min(size, Paging.MAX_PAGE_SIZE)
      size = max(size, 1)

      val sorting = this.toSorting(entity)

      return PageRequest.of(page, size).withSort(sorting)
    }
  }
}
