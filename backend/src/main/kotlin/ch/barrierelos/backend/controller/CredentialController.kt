package ch.barrierelos.backend.controller

import ch.barrierelos.backend.constant.Endpoint.CREDENTIAL
import ch.barrierelos.backend.constant.MediaType
import ch.barrierelos.backend.model.Credential
import ch.barrierelos.backend.service.CredentialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
public class CredentialController
{
  @Autowired
  private lateinit var credentialService: CredentialService

  @PutMapping(value = ["$CREDENTIAL/{id}"], consumes = [MediaType.JSON], produces = [MediaType.JSON])
  public fun updateCredential(@PathVariable id: Long, @RequestBody credential: Credential): ResponseEntity<Void>
  {
    credential.userId = id

    val credential: Credential = this.credentialService.updateCredential(credential)

    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
