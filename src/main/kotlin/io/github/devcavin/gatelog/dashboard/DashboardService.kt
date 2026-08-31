package io.github.devcavin.gatelog.dashboard

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.dashboard.dto.DashboardFeed
import io.github.devcavin.gatelog.dashboard.dto.DashboardSummary
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.VisitRepository
import io.github.devcavin.gatelog.visitors.VisitResponseMapper
import io.github.devcavin.gatelog.visitors.VisitStatusRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DashboardService(
    private val visitRepository: VisitRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val authorizationService: AuthorizationService,
    private val timeUtil: TimeUtil,
    private val visitResponseMapper: VisitResponseMapper
) {

    @Transactional(readOnly = true)
    fun getFeed(requestedBy: User): DashboardFeed {

        val scope = authorizationService.scopeFor(requestedBy)
        val siteId = scope.siteIdOrNull

        val startOfToday = timeUtil.startOfToday()
        val endOfTheDay = timeUtil.endOfToday()

        val checkedInStatus =
            visitStatusRepository.findByName("CHECKED_IN")
                ?: throw ResourceNotFoundException(
                    "Visit Status",
                    "CHECKED_IN"
                )

        val checkedOutStatus =
            visitStatusRepository.findByName("CHECKED_OUT")
                ?: throw ResourceNotFoundException(
                    "Visit Status",
                    "CHECKED_OUT"
                )

        val overdueStatus =
            visitStatusRepository.findByName("OVERDUE")
                ?: throw ResourceNotFoundException(
                    "Visit Status",
                    "OVERDUE"
                )

        val currentlyOnPremises =
            visitRepository.countCurrentlyOnPremises(
                siteId = siteId,
                checkedInStatus = checkedInStatus,
                overdueStatus = overdueStatus
            )

        val checkedInToday =
            visitRepository.countCheckedInToday(
                siteId = siteId,
                startOfDay = startOfToday,
                endOfDay = endOfTheDay
            )

        val checkedOutToday =
            visitRepository.countCheckedOutToday(
                siteId = siteId,
                checkedOutStatus = checkedOutStatus,
                startOfDay = startOfToday,
                endOfDay = endOfTheDay
            )

        val overdueCount =
            visitRepository.countOverdue(
                siteId = siteId,
                overdueStatus = overdueStatus
            )

        val overnightCount =
            visitRepository.countOvernight(
                siteId = siteId,
                startOfToday = startOfToday
            )

        val activeVisitors =
            visitRepository.findActiveVisitors(
                siteId = siteId,
                checkedInStatus = checkedInStatus,
                pageable = PageRequest.of(0, 10)
            ).content

        val overdueVisitors =
            visitRepository.findOverdueVisitors(
                siteId = siteId,
                overdueStatus = overdueStatus,
                pageable = PageRequest.of(0, 10)
            ).content

        val overnightVisitors =
            visitRepository.findOvernightVisitors(
                siteId = siteId,
                startOfToday = startOfToday,
                pageable = PageRequest.of(0, 10)
            ).content

        val recentlyCheckedOut =
            visitRepository.findRecentlyCheckedOut(
                siteId = siteId,
                checkedOutStatus = checkedOutStatus,
                pageable = PageRequest.of(0, 10)
            ).content

        return DashboardFeed(
            summary = DashboardSummary(
                currentlyOnPremises = currentlyOnPremises,
                checkedInToday = checkedInToday,
                checkedOutToday = checkedOutToday,
                overdueCount = overdueCount,
                overnightCount = overnightCount,
                asOf = timeUtil.timeNow()
            ),
            activeVisitors = activeVisitors.map(visitResponseMapper::toResponse),
            overdueVisitors = overdueVisitors.map(visitResponseMapper::toResponse),
            overnightVisitors = overnightVisitors.map(visitResponseMapper::toResponse),
            recentlyCheckedOut = recentlyCheckedOut.map(visitResponseMapper::toResponse),
        )
    }
}