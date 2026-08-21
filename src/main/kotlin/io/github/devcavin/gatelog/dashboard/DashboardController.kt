package io.github.devcavin.gatelog.dashboard

import io.github.devcavin.gatelog.dashboard.dto.DashboardFeed
import io.github.devcavin.gatelog.users.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {
    @GetMapping
    fun getFeed(
        @AuthenticationPrincipal requestedBy: User,
    ): ResponseEntity<DashboardFeed> {
        val feed = dashboardService.getFeed(requestedBy)

        return ResponseEntity.status(HttpStatus.OK)
            .body(feed)
    }
}