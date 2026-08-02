package io.github.devcavin.gatelog.backend.visitors.dto

import io.github.devcavin.gatelog.backend.visitors.Visitor
import java.time.OffsetDateTime
import java.util.UUID

data class VisitorResponse(
    val id: UUID,
    val name: String,
    val phone: String,
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

data class ReturningVisitorResponse(
    val name: String,
    val phone: String,
    val visitorType: String,
    val zoneId: UUID?,
    val zoneName: String?
)

data class VisitorProfileResponse(
    val id: UUID,
    val name: String,
    val phoneNumber: String,
    val siteId: UUID,
    val visitCount: Int
)

fun Visitor.toResponse() = VisitorResponse(
    id = id!!,
    name = name,
    phone = phone,
    visitorType = visitorType,
    purpose = purpose,
    status = visitStatus.name,
    siteId = site.id!!,
    zoneId = zone?.id,
    zoneName = zone?.name,
    createdById = createdBy.id!!,
    createdByName = createdBy.name,
    checkInTime = checkInTime,
    checkOutTime = checkOutTime
)