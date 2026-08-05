package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.InvalidStateException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.*
import io.github.devcavin.gatelog.backend.zones.ZoneRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@Service
class VisitorService(
    private val visitorRepository: VisitorRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional
    fun register(requestedBy: User, request: RegisterVisitorRequest): VisitorResponse {
        val checkedInStatus = visitStatusRepository.findByName("CHECKED_IN")
            ?: throw ResourceNotFoundException("VisitStatus", "CHECKED_IN")

        // registration always scoped to the registering user's own site
        val registrationSiteId = requestedBy.site.id!!

        val zone = request.zoneId.let {
            zoneRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Zone", it) }
                .also { z ->
                    if (z.site.id != registrationSiteId)
                        throw AccessDeniedException("Zone does not belong to your site")
                }
        }

        val profile = visitorProfileRepository
            .findBySiteIdAndPhoneNumber(registrationSiteId, request.phone)
            ?: visitorProfileRepository.save(
                VisitorProfile(
                    name = request.name,
                    phoneNumber = request.phone,
                    site = requestedBy.site
                )
            )

        val visitor = Visitor(
            name = request.name,
            phone = request.phone,
            visitorProfile = profile,
            site = requestedBy.site,
            zone = zone,
            createdBy = requestedBy,
            visitStatus = checkedInStatus,
            visitorType = request.visitorType,
            purpose = request.purpose
        )

        return visitorRepository.save(visitor).toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(requestedBy: User, visitorId: UUID): VisitorResponse {
        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow { ResourceNotFoundException("Visitor", visitorId) }
        authorizationService.assertCanAccessVisitor(requestedBy, visitor)
        return visitor.toResponse()
    }

    @Transactional
    fun checkOut(requestedBy: User, visitorId: UUID): VisitorResponse {
        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow { ResourceNotFoundException("Visitor", visitorId) }

        authorizationService.assertCanAccessVisitor(requestedBy, visitor)

        if (visitor.visitStatus.name != "CHECKED_IN") {
            throw InvalidStateException(
                "Visitor is already ${visitor.visitStatus.name
                    .lowercase().replace('_', ' ')}"
            )
        }

        val checkedOutStatus = visitStatusRepository.findByName("CHECKED_OUT")
            ?: throw ResourceNotFoundException("VisitStatus", "CHECKED_OUT")

        visitor.visitStatus = checkedOutStatus
        visitor.checkOutTime = OffsetDateTime.now()
        return visitorRepository.save(visitor).toResponse()
    }

    @Transactional(readOnly = true)
    fun search(
        requestedBy: User,
        params: VisitorSearchParams,
        pageable: Pageable
    ): Page<VisitorResponse> {
        val scope = authorizationService.scopeFor(requestedBy)
        val spec = VisitorSpecification.search(scope, params)
        return visitorRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findReturningVisitor(
        requestedBy: User,
        phone: String
    ): ReturningVisitorResponse? {
        // returning visitor lookup is always site-scoped
        // even SUPER_ADMIN registers visitors at their own site
        val siteId = requestedBy.site.id!!

        val profile = visitorProfileRepository
            .findBySiteIdAndPhoneNumber(siteId, phone)
            ?: return null

        val lastVisit = visitorRepository
            .findTopBySiteIdAndPhoneOrderByCheckInTimeDesc(siteId, phone)

        return ReturningVisitorResponse(
            name = profile.name,
            phone = profile.phoneNumber,
            visitorType = lastVisit?.visitorType ?: "",
            zoneId = lastVisit?.zone?.id,
            zoneName = lastVisit?.zone?.name
        )
    }

    @Transactional
    fun updateProfile(
        requestedBy: User,
        profileId: UUID,
        request: UpdateVisitorProfileRequest
    ): VisitorProfileResponse {
        val profile = visitorProfileRepository.findById(profileId)
            .orElseThrow { ResourceNotFoundException("VisitorProfile", profileId) }

        // profile updates always scoped to the user's own site
        if (profile.site.id != requestedBy.site.id) {
            throw AccessDeniedException("Profile does not belong to your site")
        }

        if (request.phoneNumber != profile.phoneNumber &&
            visitorProfileRepository.existsBySiteIdAndPhoneNumber(
                requestedBy.site.id!!, request.phoneNumber
            )
        ) {
            throw ConflictException(
                "Phone '${request.phoneNumber}' already registered at this site"
            )
        }

        profile.name = request.name
        profile.phoneNumber = request.phoneNumber

        val saved = visitorProfileRepository.save(profile)
        val visitCount = visitorRepository
            .countBySiteIdAndVisitorProfileId(requestedBy.site.id!!, profileId)

        return VisitorProfileResponse(
            id = saved.id!!,
            name = saved.name,
            phoneNumber = saved.phoneNumber,
            siteId = saved.site.id!!,
            visitCount = visitCount.toInt()
        )
    }

    private fun Visitor.toResponse() = VisitorResponse(
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
}