package io.github.devcavin.gatelog.reports

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.Visit
import io.github.devcavin.gatelog.visitors.VisitRepository
import io.github.devcavin.gatelog.visitors.VisitSpecification
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.time.Duration
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val visitRepository: VisitRepository,
    private val authorizationService: AuthorizationService,
    private val timeUtil: TimeUtil
) {

    private val formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss"
    )

    @Transactional(readOnly = true)
    fun exportVisitorsCsv(
        requestedBy: User,
        params: VisitSearchParams
    ): ByteArray {
        val scope = authorizationService.scopeFor(requestedBy)

        val specification = VisitSpecification.search(
            scope = scope,
            params = params
        )

        val visits = visitRepository.findAll(specification)

        return buildCsv(visits)
    }

    private fun buildCsv(visits: List<Visit>): ByteArray {
        val output = ByteArrayOutputStream()

        PrintWriter(output).use { writer ->
            writer.println(
                csvRow(
                    "ID",
                    "Name",
                    "Phone",
                    "Visitor Type",
                    "Purpose",
                    "Status",
                    "Zone",
                    "Host",
                    "Registered By",
                    "Site",
                    "Check In",
                    "Check Out",
                    "Duration (minutes)",
                    "Overnight"
                )
            )

            visits.forEach { visit ->
                writer.println(
                    csvRow(
                        visit.id.toString(),
                        visit.visitorProfile.name,
                        visit.visitorProfile.phoneNumber,
                        visit.visitorType,
                        visit.purpose,
                        visit.visitStatus.name,
                        visit.zone?.name ?: "",
                        "",
                        visit.createdBy.name,
                        visit.site.name,
                        formatter.format(visit.checkInTime),
                        visit.checkOutTime
                            ?.let(formatter::format)
                            ?: "",
                        durationMinutes(visit),
                        timeUtil.isOvernight(visit.checkInTime).toString()
                    )
                )
            }
        }

        return output.toByteArray()
    }

    private fun durationMinutes(visit: Visit): String =
        visit.checkOutTime
            ?.let { checkOutTime ->
                Duration
                    .between(visit.checkInTime, checkOutTime)
                    .toMinutes()
                    .toString()
            }
            ?: ""

    private fun csvRow(vararg fields: String): String =
        fields.joinToString(",") { field ->
            "\"${field.replace("\"", "\"\"")}\""
        }
}
