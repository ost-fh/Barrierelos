package ch.barrierelos.backend.job

import ch.barrierelos.backend.entity.DefaultParameters
import ch.barrierelos.backend.enums.OrderEnum
import ch.barrierelos.backend.service.WebsiteScanService
import ch.barrierelos.backend.service.WebsiteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.ceil

public const val SECOND: Long = 1000L
public const val MINUTE: Long = 60 * SECOND
public const val HOUR: Long = 60 * MINUTE

public const val ADMIN_ID: Long = 1L

@Service
public class RescanJob
{
  @Autowired
  private lateinit var websiteService: WebsiteService
  @Autowired
  private lateinit var websiteScanService: WebsiteScanService

  @Scheduled(fixedDelay = HOUR, initialDelay = SECOND)
  public fun start()
  {
    val websiteIds = getWorkAsWebsiteIds()

    for(websiteId in websiteIds)
    {
      this.websiteService.scanWebsite(websiteId, ADMIN_ID)
    }
  }

  private fun getWorkAsWebsiteIds(): Set<Long>
  {
    val cyclesLeftInMonth = cyclesLeftInMonth()

    val websites = this.websiteService.getWebsites(defaultParameters = DefaultParameters(sort = "modified", order = OrderEnum.ASC)).content.toSet()

    val firstTimestampOfMonth = firstTimestampOfMonth()

    val websitesIdsAlreadyDoneThisMonth = this.websiteScanService
      .getWebsiteScans().content
      .filter { it.modified > firstTimestampOfMonth }
      .map { it.website.id }
      .toSet()

    val websiteIdsLeftTodoThisMonth = websites.map { it.id }.toMutableSet().also { it.removeAll(websitesIdsAlreadyDoneThisMonth) }

    val workload = ceil(websiteIdsLeftTodoThisMonth.size.toDouble() / cyclesLeftInMonth).toInt()

    return if(workload >= websiteIdsLeftTodoThisMonth.size)
    {
      websiteIdsLeftTodoThisMonth
    }
    else
    {
      websiteIdsLeftTodoThisMonth.toList().subList(0, workload).toSet()
    }
  }

  private fun cyclesLeftInMonth(): Long
  {
    val now = LocalDate.now()

    val firstDateOfMonth = now.withDayOfMonth(1)
    val lastDateOfMonth = now.withDayOfMonth(now.lengthOfMonth())

    val firstTimeOfMonth = firstDateOfMonth.atStartOfDay()
    val lastTimeOfMonth = lastDateOfMonth.atTime(23, 59, 59, 999999999)

    return Duration.between(firstTimeOfMonth, lastTimeOfMonth).toHours()
  }

  private fun firstTimestampOfMonth(): Long
  {
    val now = LocalDate.now()

    val firstTimeOfMonth = now.withDayOfMonth(1).atStartOfDay()

    return firstTimeOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
  }
}
