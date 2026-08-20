package io.github.devcavin.gatelog.backend.visitors.dto

import io.github.devcavin.gatelog.backend.visitors.Visitor
import io.github.devcavin.gatelog.backend.visitors.VisitorProfile
import java.time.OffsetDateTime
import java.util.UUID

data class VisitorProfileSummary(
    val id: UUID,
    val name: String,
    val phoneNumber: String
)

data class VisitorResponse(
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
    val checkInTime: OffsetDateTime,
    val checkOutTime: OffsetDateTime?
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
    val checkInTime: OffsetDateTime,
    val checkOutTime: OffsetDateTime?
)

fun Visitor.toResponse(): VisitorResponse {
    val profile = visitorProfile

    return VisitorResponse(
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
        checkOutTime = checkOutTime
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

fun Visitor.toVisitSummary(): VisitSummary =
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