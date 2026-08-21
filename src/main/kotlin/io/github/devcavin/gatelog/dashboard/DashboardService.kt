package io.github.devcavin.gatelog.dashboard

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.dashboard.dto.DashboardFeed
import io.github.devcavin.gatelog.dashboard.dto.DashboardSummary
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.VisitStatusRepository
import io.github.devcavin.gatelog.visitors.VisitRepository
import io.github.devcavin.gatelog.visitors.dto.toResponse
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
    private val visitRepository: VisitRepository,
    private val visitorStatusRepository: VisitStatusRepository,
    private val authorizationService: AuthorizationService,
    @Value($$"${gatelog.scheduler.overdue-threshold-hours:2}")
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
            visitRepository.countBySiteIdAndVisitStatus(siteId, checkedInStatus) + visitRepository.countBySiteIdAndVisitStatus(siteId, overdueStatus)
        } else {
            visitRepository.countByVisitStatus(checkedInStatus) + visitRepository.countByVisitStatus(overdueStatus)
        }

        val checkedInToday = if (siteId != null) {
            visitRepository.findAllCheckedInToday(
                siteId, startOfDay, endOfDay, PageRequest.of(0, 1)
            ).totalElements
        } else {
            visitRepository.countCheckedInTodayGlobal(startOfDay, endOfDay)
        }

        val checkedOutToday = if (siteId != null) {
            visitRepository.countBySiteIdAndVisitStatusAndCheckOutTimeBetween(siteId, checkedOutStatus, startOfDay, endOfDay)
        } else {
            visitRepository.countByVisitStatusAndCheckOutTimeBetween(checkedOutStatus, startOfDay, endOfDay)
        }

        val overdueCount = if (siteId != null) {
            visitRepository.findAllBySiteIdAndVisitStatus(
                siteId, overdueStatus, PageRequest.of(0, 1)
            ).totalElements
        } else {
            visitRepository.countByVisitStatus(overdueStatus)
        }

        val activeVisitors = if (siteId != null) {
            visitRepository.findAllBySiteIdAndVisitStatus(
                siteId, checkedInStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkInTime"))
            ).content
        } else {
            visitRepository.findAllByVisitStatus(
                checkedInStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkInTime"))
            ).content
        }

        val overdueVisitors = if (siteId != null) {
            visitRepository.findAllOverdue(siteId, overdueThreshold)
        } else {
            visitRepository.findAllOverdueGlobal(overdueThreshold)
        }

        val recentlyCheckedOut = if (siteId != null) {
            visitRepository.findAllBySiteIdAndVisitStatus(
                siteId, checkedOutStatus,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "checkOutTime"))
            ).content
        } else {
            visitRepository.findAllByVisitStatus(
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