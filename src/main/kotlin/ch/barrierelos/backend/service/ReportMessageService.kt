package ch.barrierelos.backend.service

import ch.barrierelos.backend.converter.toEntity
import ch.barrierelos.backend.converter.toModel
import ch.barrierelos.backend.entity.ReportMessageEntity
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.model.ReportMessage
import ch.barrierelos.backend.parameter.DefaultParameters
import ch.barrierelos.backend.repository.ReportMessageRepository
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByReportFk
import ch.barrierelos.backend.repository.ReportMessageRepository.Companion.findAllByUserFk
import ch.barrierelos.backend.repository.Repository.Companion.findAll
import ch.barrierelos.backend.security.Security
import ch.barrierelos.backend.util.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class ReportMessageService
{
  @Autowired
  private lateinit var reportMessageRepository: ReportMessageRepository

  public fun addReportMessage(reportMessage: ReportMessage): ReportMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    val timestamp = System.currentTimeMillis()
    reportMessage.created = timestamp
    reportMessage.modified = timestamp

    return this.reportMessageRepository.save(reportMessage.toEntity()).toModel(reportMessage)
  }

  public fun updateReportMessage(reportMessage: ReportMessage): ReportMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    val existingReportMessage = this.reportMessageRepository.findById(reportMessage.id).orElseThrow().toModel()

    reportMessage.modified = System.currentTimeMillis()
    reportMessage.created = existingReportMessage.created

    return this.reportMessageRepository.save(reportMessage.toEntity()).toModel()
  }

  public fun getReportMessages(defaultParameters: DefaultParameters = DefaultParameters()): Result<ReportMessage>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportMessageRepository.findAll(defaultParameters, ReportMessageEntity::class.java, ReportMessageEntity::toModel)
  }

  public fun getReportMessagesByReport(reportId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<ReportMessage>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportMessageRepository.findAllByReportFk(reportId, defaultParameters)
  }

  public fun getReportMessagesByUser(userId: Long, defaultParameters: DefaultParameters = DefaultParameters()): Result<ReportMessage>
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportMessageRepository.findAllByUserFk(userId, defaultParameters)
  }

  public fun getReportMessage(reportMessageId: Long): ReportMessage
  {
    Security.assertAnyRoles(RoleEnum.ADMIN, RoleEnum.MODERATOR, RoleEnum.CONTRIBUTOR)

    return this.reportMessageRepository.findById(reportMessageId).orElseThrow().toModel()
  }

  public fun deleteReportMessage(reportMessageId: Long)
  {
    Security.assertAnyRoles(RoleEnum.ADMIN)

    this.reportMessageRepository.deleteById(reportMessageId)
  }
}
