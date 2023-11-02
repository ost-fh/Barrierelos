package ch.barrierelos.backend.repository

import ch.barrierelos.backend.message.enums.Order
import ch.barrierelos.backend.parameter.DefaultParameters
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

@NoRepositoryBean
public interface Repository<E> : JpaRepository<E, Long>
{
  public fun findAllByModifiedAfter(modified: Timestamp, pageable: Pageable): Page<E>

  public fun findAllByModifiedAfter(modified: Timestamp, sort: Sort): List<E>

  @Query("SELECT MAX(e.modified) FROM #{#entityName} e")
  public fun lastModifiedTimestamp(): Timestamp?

  public companion object
  {
    public fun <E> Repository<E>.lastModified(): Long = this.lastModifiedTimestamp()?.time ?: 0

    public fun <E> Repository<E>.checkIfExists(id: Long)
    {
      if(!this.existsById(id)) throw NoSuchElementException()
    }

    private fun <E> Repository<E>.findAllByModifiedAfter(modified: Long, pageable: Pageable): Page<E> = findAllByModifiedAfter(Timestamp(modified), pageable)

    private fun <E> Repository<E>.findAllByModifiedAfter(modified: Long, sort: Sort): List<E> = findAllByModifiedAfter(Timestamp(modified), sort)

    public fun <E, M> Repository<E>.findAll(defaultParameters: DefaultParameters, entity: Class<E>, toModel: E.() -> M): Result<M> = findAll(defaultParameters.page, defaultParameters.size, defaultParameters.sort, defaultParameters.order, defaultParameters.modifiedAfter, entity, toModel)

    public fun <E, M> Repository<E>.findAll(page: Int?, size: Int?, sort: String?, order: Order?, modifiedAfter: Long?, entity: Class<E>, toModel: E.() -> M): Result<M>
    {
      var sort = (if(sort == "id") null else sort) ?: (entity.declaredFields.find { it.isAnnotationPresent(Id::class.java) })?.name

      val fields = entity.declaredFields.map { it.name }

      if(sort != null && !fields.contains(sort))
      {
        if(sort.startsWith("is"))
        {
          sort = sort.substringAfter("is").replaceFirstChar { it.lowercase() }
        }
        else if(sort.endsWith("Id"))
        {
          sort = sort.substringBeforeLast("Id") + "Fk"
        }
      }

      var sorting = if(sort != null) Sort.by(sort) else Sort.unsorted()

      sorting = when(order)
      {
        Order.ASC -> sorting.ascending()
        Order.DESC -> sorting.descending()
        else -> sorting
      }

      val count = this.count()
      val lastModified = this.lastModified()

      return if(page != null && size != null && size > 0)
      {
        val pageable = PageRequest.of(page, size).withSort(sorting)

        if(modifiedAfter == null)
        {
          this.findAll(pageable).toResult(count, lastModified, toModel)
        }
        else
        {
          this.findAllByModifiedAfter(modifiedAfter, pageable).toResult(count, lastModified, toModel)
        }
      }
      else
      {
        if(modifiedAfter == null)
        {
          Result(this.findAll(sorting).map { it.toModel() }, count, lastModified)
        }
        else
        {
          Result(this.findAllByModifiedAfter(modifiedAfter, sorting).map { it.toModel() }, count, lastModified)
        }
      }
    }
  }
}
