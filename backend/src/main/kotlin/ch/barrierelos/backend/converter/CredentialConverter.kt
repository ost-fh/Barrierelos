package ch.barrierelos.backend.converter

import ch.barrierelos.backend.entity.CredentialEntity
import ch.barrierelos.backend.model.Credential
import java.sql.Timestamp

public fun Credential.toEntity(): CredentialEntity
{
  return CredentialEntity(
    credentialId = this.id,
    userFk = this.userId,
    password = this.password,
    issuer = this.issuer,
    subject = this.subject,
    modified = Timestamp(this.modified),
    created = Timestamp(this.created),
  )
}

public fun CredentialEntity.toModel(): Credential
{
  return Credential(
    id = this.credentialId,
    userId = this.userFk,
    password = this.password,
    issuer = this.issuer,
    subject = this.subject,
    modified = this.modified.time,
    created = this.created.time,
  )
}

public fun CredentialEntity.toModel(credential: Credential): Credential
{
  return credential.apply {
    id = this@toModel.credentialId
    userId = this@toModel.userFk
    password = this@toModel.password
    issuer = this@toModel.issuer
    subject = this@toModel.subject
    modified = this@toModel.modified.time
    created = this@toModel.created.time
  }
}
