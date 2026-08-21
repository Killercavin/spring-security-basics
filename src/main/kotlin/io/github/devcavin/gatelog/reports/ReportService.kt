package io.github.devcavin.gatelog.reports

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.Visit
import io.github.devcavin.gatelog.visitors.VisitRepository
import io.github.devcavin.gatelog.visitors.VisitSpecification
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.PrintWriter
import java.time.Duration
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val visitRepository: VisitRepository,
    private val authorizationService: AuthorizationService
) {

    private val formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.UTC)

    @Transactional(readOnly = true)
    fun exportVisitorsCsv(
        requestedBy: User,
        params: VisitSearchParams
    ): ByteArray {
        val scope = authorizationService.scopeFor(requestedBy)
        val spec = VisitSpecification.search(scope, params)
        val visitors = visitRepository.findAll(spec)
        return buildCsv(visitors)
    }

    private fun buildCsv(visitors: List<Visit>): ByteArray {
        val out = ByteArrayOutputStream()
        val writer = PrintWriter(out)

        writer.println(
            csvRow(
                "ID", "Name", "Phone", "Visitor Type",
                "Purpose", "Status", "Zone", "Host",
                "Registered By", "Site",
                "Check In", "Check Out", "Duration (minutes)"
            )
        )

        visitors.forEach { v ->
            val duration = v.checkOutTime?.let {
                Duration.between(v.checkInTime, it).toMinutes().toString()
            } ?: ""

            writer.println(
                csvRow(
                    v.id.toString(),
                    v.visitorType,
                    v.purpose,
                    v.visitStatus.name,
                    v.zone?.name ?: "",
                    v.createdBy.name,
                    v.site.name,
                    formatter.format(v.checkInTime),
                    v.checkOutTime?.let { formatter.format(it) } ?: "",
                    duration
                )
            )
        }

        writer.flush()
        return out.toByteArray()
    }

    private fun csvRow(vararg fields: String): String =
        fields.joinToString(",") { field ->
            '"' + field.replace("\"", "\"\"") + '"'
        }
}