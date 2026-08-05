package io.github.devcavin.gatelog.backend.dashboard

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.dashboard.dto.DashboardFeed
import io.github.devcavin.gatelog.backend.dashboard.dto.DashboardSummary
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.VisitStatusRepository
import io.github.devcavin.gatelog.backend.visitors.VisitorRepository
import io.github.devcavin.gatelog.backend.visitors.dto.toResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class DashboardService(
    private val visitorRepository: VisitorRepository,
    private val visitorStatusRepository: VisitStatusRepository,
    private val authorizationService: AuthorizationService,
    @Value("\${gatelog.scheduler.overdue-threshold-hours:2}")
    private val overdueThresholdHours: Long,
) {
    @Transactional(readOnly = true)
    fun getFeed(requestedBy: User): DashboardFeed {
        val scope = authorizationService.scopeFor(requestedBy)
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val startOfDay = now.toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)
        val endOfDay = startOfDay.plusDays(1)
        val overdueThreshold = now.minusHours(overdueThresholdHours)

        val checkedInStatus  = visitorStatusRepository.findByName("CHECKED_IN")!!
        val checkedOutStatus = visitorStatusRepository.findByName("CHECKED_OUT")!!
        val overdueStatus    = visitorStatusRepository.findByName("OVERDUE")!!

        // siteId is null for SUPER_ADMIN (Global scope)
        // siteId is set for MANAGER and STAFF (Site scope)
        val siteId: UUID? = scope.siteIdOrNull

        val currentlyOnPremises = if (siteId != null) {
            visitorRepository.countBySiteIdAndVisitStatus(siteId, checkedInStatus) + visitorRepository.countBySiteIdAndVisitStatus(siteId, overdueStatus)
        } else {
            visitorRepository.countByVisitStatus(checkedInStatus) + visitorRepository.countByVisitStatus(overdueStatus)
        }

        val checkedInToday = if (siteId != null) {
            visitorRepository.findAllCheckedInToday(
                siteId, startOfDay, endOfDay, PageRequest.of(0, 1)
            ).totalElements
        } else {
            visitorRepository.countCheckedInTodayGlobal(startOfDay, endOfDay)
        }

        val checkedOutToday = if (siteId != null) {
            visitorRepository.countBySiteIdAndVisitStatusAndCheckOutTimeBetween(siteId, checkedOutStatus, startOfDay, endOfDay)
        } else {
            visitorRepository.countByVisitStatusAndCheckOutTimeBetween(checkedOutStatus, startOfDay, endOfDay)
        }

        val overdueCount = if (siteId != null) {
            visitorRepository.findAllBySiteIdAndVisitStatus(
                siteId, overdueStatus, PageRequest.of(0, 1)
            ).totalElements
        } else {
            visitorRepository.countByVisitStatus(overdueStatus)
        }

        val activeVisitors = if (siteId != null) {
            visitorRepository.findAllBySiteIdAndVisitStatus(
                siteId, checkedInStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkInTime"))
            ).content
        } else {
            visitorRepository.findAllByVisitStatus(
                checkedInStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkInTime"))
            ).content
        }

        val overdueVisitors = if (siteId != null) {
            visitorRepository.findAllOverdue(siteId, overdueThreshold)
        } else {
            visitorRepository.findAllOverdueGlobal(overdueThreshold)
        }

        val recentlyCheckedOut = if (siteId != null) {
            visitorRepository.findAllBySiteIdAndVisitStatus(
                siteId, checkedOutStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkOutTime"))
            ).content
        } else {
            visitorRepository.findAllByVisitStatus(
                checkedOutStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkOutTime"))
            ).content
        }

        return DashboardFeed(
            summary = DashboardSummary(
                currentlyOnPremises = currentlyOnPremises,
                checkedInToday      = checkedInToday,
                checkedOutToday     = checkedOutToday,
                overdueCount        = overdueCount
            ),
            activeVisitors     = activeVisitors.map { it.toResponse() },
            overdueVisitors    = overdueVisitors.map { it.toResponse() },
            recentlyCheckedOut = recentlyCheckedOut.map { it.toResponse() }
        )
    }
}