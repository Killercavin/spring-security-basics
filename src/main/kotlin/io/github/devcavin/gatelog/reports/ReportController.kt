package io.github.devcavin.gatelog.reports

import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportService: ReportService) {
    @GetMapping("/visitors/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun exportVisitorsCsv(
        @AuthenticationPrincipal requestedBy: User,
        @RequestParam(required = false) from: OffsetDateTime?,
        @RequestParam(required = false) to: OffsetDateTime?,
        @RequestParam(required = false) visitorType: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) zoneId: UUID?
    ): ResponseEntity<ByteArray> {
        val params = VisitSearchParams(
            from = from,
            to = to,
            visitorType = visitorType,
            status = status,
            zoneId = zoneId
        )

        val csv = reportService.exportVisitorsCsv(requestedBy, params)
        val filename = buildFilename(from, to)

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"$filename\""
            )
            .contentType(MediaType("text", "csv"))
            .body(csv)
    }

    private fun buildFilename(
        from: OffsetDateTime?,
        to: OffsetDateTime?
    ): String {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fromLabel = from?.let { fmt.format(it) }
            ?: fmt.format(LocalDate.now(ZoneOffset.UTC))
        val toLabel = to?.let { fmt.format(it) }
            ?: fmt.format(LocalDate.now(ZoneOffset.UTC))
        return "gatelog-visitors-$fromLabel-to-$toLabel.csv"
    }
}