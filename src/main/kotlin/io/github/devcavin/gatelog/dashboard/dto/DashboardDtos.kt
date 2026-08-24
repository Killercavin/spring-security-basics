package io.github.devcavin.gatelog.dashboard.dto

import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.visitors.dto.VisitResponse
import java.time.OffsetDateTime

data class DashboardSummary(
    val currentlyOnPremises: Long,
    val checkedInToday: Long,
    val checkedOutToday: Long,
    val overdueCount: Long,
    val overnightCount: Long,
    val asOf: OffsetDateTime = TimeUtil.timeNow()
)

data class DashboardFeed(
    val summary: DashboardSummary,
    val activeVisitors: List<VisitResponse>,
    val overdueVisitors: List<VisitResponse>,
    val overnightVisitors: List<VisitResponse>,
    val recentlyCheckedOut: List<VisitResponse>
)