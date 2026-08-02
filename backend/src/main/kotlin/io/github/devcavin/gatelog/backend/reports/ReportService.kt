package io.github.devcavin.gatelog.backend.reports

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
    private val visitorRepository: VisitorRepository
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC)

    @Transactional(readOnly = true)
    fun exportVisitorsCsv(
        requestedBy: User,
        searchParams: VisitorSearchParams
    ): ByteArray {
        val spec = VisitorSpecification.search(
            siteId = requestedBy.site.id!!,
            params = searchParams
        )

        val visitors = visitorRepository.findAll(spec)

        return buildCsv(visitors)
    }

    private fun buildCsv(visitors: List<Visitor>): ByteArray {
        val output = ByteArrayOutputStream()
        val writer = PrintWriter(output)

        writer.println(
            csvRow (
                "ID", "Name", "Phone", "Visitor Type",
                "Purpose", "Status", "Zone", "Host",
                "Registered By", "Check In", "Check Out", "Duration (minutes)"
                )
        )

        visitors.forEach { visitor ->
            val durationInMinutes = visitor.checkOutTime?.let {
                Duration.between(visitor.checkInTime, it).toMinutes().toString()
            } ?: ""

            writer.println(
                csvRow(
                    visitor.id.toString(),
                    visitor.name,
                    visitor.phone,
                    visitor.visitorType,
                    visitor.purpose,
                    visitor.visitStatus.name,
                    visitor.zone?.name ?: "",
                    visitor.createdBy.name,
                    formatter.format(visitor.checkInTime),
                    visitor.checkOutTime?.let { formatter.format(it) } ?: "",
                    durationInMinutes
                )
            )
        }

        writer.flush()
        return output.toByteArray()
    }

    private fun csvRow(vararg fields: String): String =
        fields.joinToString(",") { field ->
            '"' + field.replace("\"", "\"\"") + '"'
        }
}