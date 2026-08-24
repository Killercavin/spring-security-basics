package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ConflictException
import io.github.devcavin.gatelog.common.exception.InvalidStateException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.common.time.TimeProvider
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.dto.*
import io.github.devcavin.gatelog.zones.ZoneRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private const val CHECKED_IN = "CHECKED_IN"
private const val CHECKED_OUT = "CHECKED_OUT"

@Service
class VisitService(
    private val visitRepository: VisitRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val authorizationService: AuthorizationService,
    private val timeProvider: TimeProvider
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

        val zone = zoneRepository.findByIdAndSiteId(
            request.zoneId,
            siteId
        ) ?: throw ResourceNotFoundException(
            "Zone",
            request.zoneId
        )

        val profile =
            visitorProfileRepository.findBySiteIdAndPhoneNumber(
                siteId,
                request.phone
            ) ?: visitorProfileRepository.save(
                VisitorProfile(
                    name = request.name,
                    phoneNumber = request.phone,
                    site = site
                )
            )

        val profileId = requireNotNull(profile.id) {
            "Visitor profile has no ID"
        }

        val checkedInVisit =
            visitRepository.findFirstByVisitorProfileIdAndVisitStatusName(
                profileId,
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
            purpose = request.purpose,
            checkInTime = timeProvider.timeNow()
        )

        return visitRepository
            .save(visit)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        visitId: UUID
    ): VisitResponse =
        findAccessibleVisit(
            requestedBy,
            visitId
        ).toResponse()

    @Transactional(readOnly = true)
    fun search(
        requestedBy: User,
        params: VisitSearchParams,
        pageable: Pageable
    ): Page<VisitResponse> {
        val scope = authorizationService.scopeFor(requestedBy)

        return visitRepository
            .findAll(
                VisitSpecification.search(
                    scope,
                    params
                ),
                pageable
            )
            .map { it.toResponse() }
    }

    @Transactional
    fun checkOut(
        requestedBy: User,
        visitId: UUID
    ): VisitResponse {
        val visit = findAccessibleVisit(
            requestedBy,
            visitId
        )

        if (visit.visitStatus.name != CHECKED_IN) {
            throw InvalidStateException(
                "Visitor is already ${
                    visit.visitStatus.name
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

        visit.visitStatus = checkedOutStatus
        visit.checkOutTime = timeProvider.timeNow()

        return visitRepository
            .save(visit)
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

        val profile =
            visitorProfileRepository.findBySiteIdAndPhoneNumber(
                siteId,
                phone
            ) ?: return null

        val profileId = requireNotNull(profile.id) {
            "Visitor profile has no ID"
        }

        val lastVisit =
            visitRepository
                .findTopByVisitorProfileIdOrderByCheckInTimeDesc(
                    profileId
                )

        return ReturningVisitorResponse(
            profile = VisitorProfileSummary(
                id = profileId,
                name = profile.name,
                phoneNumber = profile.phoneNumber
            ),
            lastVisit = lastVisit?.toVisitSummary()
        )
    }

    /**
     * Loads a visit and verifies that the authenticated user
     * is authorized to access it.
     *
     * Individual visit operations should use this helper
     * rather than performing an unprotected repository lookup.
     */
    private fun findAccessibleVisit(
        requestedBy: User,
        visitId: UUID
    ): Visit {
        val visit = visitRepository.findById(visitId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Visit",
                    visitId
                )
            }

        authorizationService.assertCanAccessVisit(
            requestedBy,
            visit
        )

        return visit
    }
}
