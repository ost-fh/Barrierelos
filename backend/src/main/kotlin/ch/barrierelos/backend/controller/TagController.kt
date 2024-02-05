package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.TAG
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.model.Tag
import ch.barrierelos.backend.service.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
public class TagController
{
  @Autowired
  private lateinit var tagService: TagService

  @PostMapping(value = [TAG], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun addTag(@RequestBody tag: Tag): ResponseEntity<Tag>
  {
    tag.id = 0

    this.tagService.addTag(tag)

    return ResponseEntity.status(HttpStatus.CREATED).body(tag)
  }

  @PutMapping(value = ["$TAG/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateTag(@PathVariable id: Long, @RequestBody tag: Tag): ResponseEntity<Tag>
  {
    tag.id = id

    this.tagService.updateTag(tag)

    return ResponseEntity.status(HttpStatus.OK).body(tag)
  }

  @GetMapping(value = [TAG], produces = [MediaType.JSON])
  public fun getTags(): ResponseEntity<Set<Tag>>
  {
    val tags = this.tagService.getTags()

    return ResponseEntity.status(HttpStatus.OK).body(tags)
  }

  @GetMapping(value = ["$TAG/{id}"], produces = [MediaType.JSON])
  public fun getTag(@PathVariable id: Long): ResponseEntity<Tag>
  {
    val tag: Tag = this.tagService.getTag(id)

    return ResponseEntity.status(HttpStatus.OK).body(tag)
  }

  @DeleteMapping(value = ["$TAG/{id}"], produces = [MediaType.JSON])
  public fun deleteTag(@PathVariable id: Long): ResponseEntity<Void>
  {
    this.tagService.deleteTag(id)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
