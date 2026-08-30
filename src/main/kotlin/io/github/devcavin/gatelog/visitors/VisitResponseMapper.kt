package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.visitors.dto.VisitResponse
import io.github.devcavin.gatelog.visitors.dto.VisitorProfileSummary
import org.springframework.stereotype.Component

@Component
class VisitResponseMapper(
    private val timeUtil: TimeUtil
) {

    fun toResponse(visit: Visit): VisitResponse {
        val profile = visit.visitorProfile

        return VisitResponse(
            id = requireNotNull(visit.id),
            profile = VisitorProfileSummary(
                id = requireNotNull(profile.id),
                name = profile.name,
                phoneNumber = profile.phoneNumber
            ),
            visitorType = visit.visitorType,
            purpose = visit.purpose,
            status = visit.visitStatus.name,
            siteId = requireNotNull(visit.site.id),
            zoneId = visit.zone?.id,
            zoneName = visit.zone?.name,
            createdById = requireNotNull(visit.createdBy.id),
            createdByName = visit.createdBy.name,
            checkInTime = visit.checkInTime,
            checkOutTime = visit.checkOutTime,
            overnight = visit.checkOutTime == null && timeUtil.isOvernight(visit.checkInTime)
        )
    }
}