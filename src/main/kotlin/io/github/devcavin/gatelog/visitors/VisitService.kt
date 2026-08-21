package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ConflictException
import io.github.devcavin.gatelog.common.exception.InvalidStateException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.dto.RegisterVisitRequest
import io.github.devcavin.gatelog.visitors.dto.ReturningVisitorResponse
import io.github.devcavin.gatelog.visitors.dto.VisitResponse
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import io.github.devcavin.gatelog.visitors.dto.VisitorProfileSummary
import io.github.devcavin.gatelog.visitors.dto.toResponse
import io.github.devcavin.gatelog.visitors.dto.toVisitSummary
import io.github.devcavin.gatelog.zones.ZoneRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

private const val CHECKED_IN = "CHECKED_IN"
private const val CHECKED_OUT = "CHECKED_OUT"

@Service
class VisitService(
    private val visitRepository: VisitRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: RegisterVisitRequest
    ): VisitResponse {

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

        val checkedInVisit =
            visitRepository
                .findFirstByVisitorProfileIdAndVisitStatusName(
                    requireNotNull(profile.id),
                    CHECKED_IN
                )

        if (checkedInVisit != null) {
            throw ConflictException(
                "Visitor is already checked in"
            )
        }

        val checkedInStatus =
            visitStatusRepository.findByName(CHECKED_IN)
                ?: throw ResourceNotFoundException(
                    "VisitStatus",
                    CHECKED_IN
                )

        val visit = Visit(
            visitorProfile = profile,
            site = site,
            zone = zone,
            createdBy = requestedBy,
            visitStatus = checkedInStatus,
            visitorType = request.visitorType,
            purpose = request.purpose
        )

        return visitRepository
            .save(visit)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        visitId: UUID
    ): VisitResponse {

        val visitor = visitRepository.findById(visitId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitId)
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
        params: VisitSearchParams,
        pageable: Pageable
    ): Page<VisitResponse> {

        val scope = authorizationService.scopeFor(requestedBy)

        return visitRepository
            .findAll(
                VisitSpecification.search(scope, params),
                pageable
            )
            .map { it.toResponse() }
    }

    @Transactional
    fun checkOut(
        requestedBy: User,
        visitId: UUID
    ): VisitResponse {

        val visitor = visitRepository.findById(visitId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitId)
            }

        authorizationService.assertCanAccessVisitor(
            requestedBy,
            visitor
        )

        if (visitor.visitStatus.name != CHECKED_IN) {
            throw InvalidStateException(
                "Visitor is already ${
                    visitor.visitStatus.name
                        .lowercase()
                        .replace('_', ' ')
                }"
            )
        }

        val checkedOutStatus =
            visitStatusRepository.findByName(CHECKED_OUT)
                ?: throw ResourceNotFoundException(
                    "VisitStatus",
                    CHECKED_OUT
                )

        visitor.visitStatus = checkedOutStatus
        visitor.checkOutTime = OffsetDateTime.now()

        return visitRepository
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
            visitRepository
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