package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.InvalidStateException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.RegisterVisitorRequest
import io.github.devcavin.gatelog.backend.visitors.dto.ReturningVisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.UpdateVisitorProfileRequest
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorProfileResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorSearchParams
import io.github.devcavin.gatelog.backend.visitors.dto.toResponse
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
    private val visitorProfileRepository: VisitorProfileRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: RegisterVisitorRequest
    ): VisitorResponse {
        val checkedInStatus = visitStatusRepository.findByName("CHECKED_IN")
            ?: throw ResourceNotFoundException("VisitStatus", "CHECKED_IN")

        val zone = request.zoneId.let {
            zoneRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Zone", it) }
                .also { z ->
                    if (z.site.id != requestedBy.site.id)
                        throw AccessDeniedException("Zone does not belong to your site")
                }
        }

        // find or create visitor profile by phone + site
        val profile = visitorProfileRepository
            .findBySiteIdAndPhoneNumber(requestedBy.site.id!!, request.phone)
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

        enforcesSiteBoundary(requestedBy, visitor.site.id!!)
        return visitor.toResponse()
    }

    @Transactional
    fun checkOut(requestedBy: User, visitorId: UUID): VisitorResponse {
        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow { ResourceNotFoundException("Visitor", visitorId) }

        enforcesSiteBoundary(requestedBy, visitor.site.id!!)

        if (visitor.visitStatus.name != "CHECKED_IN") {
            throw InvalidStateException(
                "Visitor is already ${visitor.visitStatus.name.lowercase().replace('_', ' ')}"
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
        val spec = VisitorSpecification.search(requestedBy.site.id!!, params)
        return visitorRepository.findAll(spec, pageable).map { it.toResponse() }
    }


    @Transactional(readOnly = true)
    fun findReturningVisitor(
        requestedBy: User,
        phone: String
    ): ReturningVisitorResponse? {
        val profile = visitorProfileRepository
            .findBySiteIdAndPhoneNumber(requestedBy.site.id!!, phone)
            ?: return null

        val lastVisit = visitorRepository
            .findTopBySiteIdAndPhoneOrderByCheckInTimeDesc(
                requestedBy.site.id!!, phone
            )

        return ReturningVisitorResponse(
            name = profile.name,
            phone = profile.phoneNumber,
            visitorType = lastVisit?.visitorType ?: "",
            zoneId = lastVisit?.zone?.id,
            zoneName = lastVisit?.zone?.name
        )
    }

    private fun enforcesSiteBoundary(requestedBy: User, visitorSiteId: UUID) {
        if (requestedBy.role.name != "SUPER_ADMIN" &&
            requestedBy.site.id != visitorSiteId
        ) {
            throw AccessDeniedException("Visitor does not belong to your site")
        }
    }

    @Transactional
    fun updateProfile(
        requestedBy: User,
        profileId: UUID,
        request: UpdateVisitorProfileRequest
    ): VisitorProfileResponse {
        val profile = visitorProfileRepository.findById(profileId)
            .orElseThrow { ResourceNotFoundException("VisitorProfile", profileId) }

        if (profile.site.id != requestedBy.site.id) {
            throw AccessDeniedException("Profile does not belong to your site")
        }

        if (request.phoneNumber != profile.phoneNumber &&
            visitorProfileRepository.existsBySiteIdAndPhoneNumber(
                requestedBy.site.id!!, request.phoneNumber
            )
        ) {
            throw ConflictException(
                "Phone number is already registered at this site"
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
}