package ch.barrierelos.backend.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "credential")
public class CredentialEntity
(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public var credentialId: Long = 0,
  public var userFk: Long = 0,
  public var password: String?,
  public var issuer: String?,
  public var subject: String?,
  public var modified: Timestamp = Timestamp(System.currentTimeMillis()),
  public var created: Timestamp = Timestamp(System.currentTimeMillis()),
)
