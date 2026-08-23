package io.github.devcavin.gatelog.visitors.dto

import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.visitors.Visit
import io.github.devcavin.gatelog.visitors.VisitorProfile
import java.time.OffsetDateTime
import java.util.UUID

data class VisitorProfileSummary(
    val id: UUID,
    val name: String,
    val phoneNumber: String
)

data class VisitResponse(
    val id: UUID,
    val profile: VisitorProfileSummary,
    val visitorType: String,
    val purpose: String,
    val status: String,
    val siteId: UUID,
    val zoneId: UUID?,
    val zoneName: String?,
    val createdById: UUID,
    val createdByName: String,
    val checkInTime: OffsetDateTime?,
    val checkOutTime: OffsetDateTime?,
    val overnight: Boolean
)

data class VisitorProfileResponse(
    val id: UUID,
    val name: String,
    val phoneNumber: String,
    val siteId: UUID,
    val visitCount: Long
)

data class ReturningVisitorResponse(
    val profile: VisitorProfileSummary,
    val lastVisit: VisitSummary?
)

data class VisitSummary(
    val id: UUID,
    val visitorType: String,
    val purpose: String,
    val status: String,
    val zoneId: UUID?,
    val zoneName: String?,
    val checkInTime: OffsetDateTime?,
    val checkOutTime: OffsetDateTime?
)

fun Visit.toResponse(): VisitResponse {
    val profile = visitorProfile

    return VisitResponse(
        id = requireNotNull(id),
        profile = VisitorProfileSummary(
            id = requireNotNull(profile.id),
            name = profile.name,
            phoneNumber = profile.phoneNumber
        ),
        visitorType = visitorType,
        purpose = purpose,
        status = visitStatus.name,
        siteId = requireNotNull(site.id),
        zoneId = zone?.id,
        zoneName = zone?.name,
        createdById = requireNotNull(createdBy.id),
        createdByName = createdBy.name,
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
        overnight = checkOutTime == null &&
                TimeUtil.isOvernight(checkInTime)
    )
}

fun VisitorProfile.toResponse(
    visitCount: Long
): VisitorProfileResponse =
    VisitorProfileResponse(
        id = requireNotNull(id),
        name = name,
        phoneNumber = phoneNumber,
        siteId = requireNotNull(site.id),
        visitCount = visitCount
    )

fun Visit.toVisitSummary(): VisitSummary =
    VisitSummary(
        id = requireNotNull(id),
        visitorType = visitorType,
        purpose = purpose,
        status = visitStatus.name,
        zoneId = zone?.id,
        zoneName = zone?.name,
        checkInTime = checkInTime,
        checkOutTime = checkOutTime
    )