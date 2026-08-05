package io.github.devcavin.gatelog.backend.reports

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.Visitor
import io.github.devcavin.gatelog.backend.visitors.VisitorRepository
import io.github.devcavin.gatelog.backend.visitors.VisitorSpecification
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorSearchParams
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.PrintWriter
import java.time.Duration
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val visitorRepository: VisitorRepository,
    private val authorizationService: AuthorizationService
) {

    private val formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.UTC)

    @Transactional(readOnly = true)
    fun exportVisitorsCsv(
        requestedBy: User,
        params: VisitorSearchParams
    ): ByteArray {
        val scope = authorizationService.scopeFor(requestedBy)
        val spec = VisitorSpecification.search(scope, params)
        val visitors = visitorRepository.findAll(spec)
        return buildCsv(visitors)
    }

    private fun buildCsv(visitors: List<Visitor>): ByteArray {
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
                    v.name,
                    v.phone,
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