package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.sites.SiteRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class OverdueVisitJob(
    private val visitRepository: VisitRepository,
    private val visitorStatusRepository: VisitStatusRepository,
    private val siteRepository: SiteRepository,

    @Value("\${gatelog.scheduler.overdue-threshold-hours:2}")
    private val overdueThresholdHours: Long
) {
    private val log = LoggerFactory.getLogger(OverdueVisitJob::class.java)

    // runs scheduler every 15 mins
    @Scheduled(fixedRateString = "\${gatelog.scheduler.overdue-job-rate-ms:900000}")
    @Transactional
    fun flagOverdueVisitors() {
        val overdueStatus = visitorStatusRepository.findByName("OVERDUE")?: throw ResourceNotFoundException("Visit Status", "OVERDUE")

        val threshold = OffsetDateTime.now(ZoneOffset.UTC).minusHours(overdueThresholdHours)

        val sites = siteRepository.findAll()

        var totalFlagged = 0

        sites.forEach { site ->
            val flagged = visitRepository.markOverdue(
                siteId = site.id!!,
                threshold = threshold,
                overdueStatus = overdueStatus
            )

            if (flagged > 0) {
                log.info("Flagged $flagged overdue visitor(s) at site $site")
            }

            totalFlagged += flagged
        }

        if (totalFlagged > 0) {
            log.info("Overdue job complete - $totalFlagged visitor(s) flagged")
        }
    }
}