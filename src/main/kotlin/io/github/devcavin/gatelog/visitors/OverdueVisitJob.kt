package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.sites.SiteRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OverdueVisitJob(
    private val visitRepository: VisitRepository,
    private val visitorStatusRepository: VisitStatusRepository,
    private val siteRepository: SiteRepository,
    private val timeUtil: TimeUtil,

    @Value($$"${gatelog.scheduler.overdue-threshold-hours:2}")
    private val overdueThresholdHours: Long
) {

    private val log = LoggerFactory.getLogger(OverdueVisitJob::class.java)

    @Scheduled(
        fixedRateString = $$"${gatelog.scheduler.overdue-job-rate-ms:900000}"
    )
    @Transactional
    fun flagOverdueVisitors() {

        val overdueStatus =
            visitorStatusRepository.findByName("OVERDUE")
                ?: throw ResourceNotFoundException(
                    "Visit Status",
                    "OVERDUE".lowercase()
                )

        val threshold =
            timeUtil.timeNow().minusHours(overdueThresholdHours)

        val sites = siteRepository.findAll()

        var totalFlagged = 0

        sites.forEach { site ->

            val siteId = requireNotNull(site.id)

            val flagged = visitRepository.markOverdue(
                threshold,
                overdueStatus
            )

            if (flagged > 0) {
                log.info(
                    "Flagged {} overdue visitor(s) at site {}",
                    flagged,
                    siteId
                )
            }

            totalFlagged += flagged
        }

        if (totalFlagged > 0) {
            log.info(
                "Overdue job complete - {} visitor(s) flagged",
                totalFlagged
            )
        }
    }
}
