package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.InvalidStateException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.RegisterVisitorRequest
import io.github.devcavin.gatelog.backend.visitors.dto.ReturningVisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorSearchParams
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorProfileSummary
import io.github.devcavin.gatelog.backend.visitors.dto.toResponse
import io.github.devcavin.gatelog.backend.visitors.dto.toVisitSummary
import io.github.devcavin.gatelog.backend.zones.ZoneRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class VisitorService(
    private val visitorRepository: VisitorRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: RegisterVisitorRequest
    ): VisitorResponse {

        val site = requestedBy.site
        val siteId = requireNotNull(site.id) {
            "Authenticated user has no site"
        }

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        val zone = zoneRepository.findById(request.zoneId)
            .orElseThrow {
                ResourceNotFoundException("Zone", request.zoneId)
            }

        if (zone.site.id != siteId) {
            throw AccessDeniedException(
                "Zone does not belong to your site"
            )
        }

        val profile =
            visitorProfileRepository
                .findBySiteIdAndPhoneNumber(
                    siteId,
                    request.phone
                )
                ?: visitorProfileRepository.save(
                    VisitorProfile(
                        name = request.name,
                        phoneNumber = request.phone,
                        site = site
                    )
                )

        val checkedInStatus =
            visitStatusRepository.findByName("CHECKED_IN")
                ?: throw ResourceNotFoundException(
                    "VisitStatus",
                    "CHECKED_IN"
                )

        val visitor = Visitor(
            visitorProfile = profile,
            site = site,
            zone = zone,
            createdBy = requestedBy,
            visitStatus = checkedInStatus,
            visitorType = request.visitorType,
            purpose = request.purpose
        )

        return visitorRepository
            .save(visitor)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        visitorId: UUID
    ): VisitorResponse {

        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitorId)
            }

        authorizationService.assertCanAccessVisitor(
            requestedBy,
            visitor
        )

        return visitor.toResponse()
    }

    @Transactional(readOnly = true)
    fun search(
        requestedBy: User,
        params: VisitorSearchParams,
        pageable: Pageable
    ): Page<VisitorResponse> {

        val scope = authorizationService.scopeFor(requestedBy)

        return visitorRepository
            .findAll(
                VisitorSpecification.search(scope, params),
                pageable
            )
            .map { it.toResponse() }
    }

    @Transactional
    fun checkOut(
        requestedBy: User,
        visitorId: UUID
    ): VisitorResponse {

        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitorId)
            }

        authorizationService.assertCanAccessVisitor(
            requestedBy,
            visitor
        )

        if (visitor.visitStatus.name != "CHECKED_IN") {
            throw InvalidStateException(
                "Visitor is already ${
                    visitor.visitStatus.name
                        .lowercase()
                        .replace('_', ' ')
                }"
            )
        }

        val checkedOutStatus =
            visitStatusRepository.findByName("CHECKED_OUT")
                ?: throw ResourceNotFoundException(
                    "VisitStatus",
                    "CHECKED_OUT"
                )

        visitor.visitStatus = checkedOutStatus
        visitor.checkOutTime = OffsetDateTime.now()

        return visitorRepository
            .save(visitor)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findReturningVisitor(
        requestedBy: User,
        phone: String
    ): ReturningVisitorResponse? {

        val siteId = requireNotNull(requestedBy.site.id) {
            "Authenticated user has no site"
        }

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        val profile =
            visitorProfileRepository
                .findBySiteIdAndPhoneNumber(
                    siteId,
                    phone
                )
                ?: return null

        val lastVisit =
            visitorRepository
                .findTopByVisitorProfileIdOrderByCheckInTimeDesc(
                    requireNotNull(profile.id)
                )

        return ReturningVisitorResponse(
            profile = VisitorProfileSummary(
                id = requireNotNull(profile.id),
                name = profile.name,
                phoneNumber = profile.phoneNumber
            ),
            lastVisit = lastVisit?.toVisitSummary()
        )
    }
}