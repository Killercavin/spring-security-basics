package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ConflictException
import io.github.devcavin.gatelog.common.exception.InvalidStateException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.common.time.TimeUtil
import io.github.devcavin.gatelog.sites.SiteRepository
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
private const val OVERDUE = "OVERDUE"

@Service
class VisitService(
    private val visitRepository: VisitRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val authorizationService: AuthorizationService,
    private val timeUtil: TimeUtil,
    private val visitResponseMapper: VisitResponseMapper,
    private val siteRepository: SiteRepository
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: RegisterVisitRequest
    ): VisitResponse {
        authorizationService.assertCovers(requestedBy, request.siteId)

        val targetSite = siteRepository.findById(request.siteId)
            .orElseThrow { ResourceNotFoundException("Site", request.siteId) }

        val zone = zoneRepository.findByIdAndSiteId(
            request.zoneId,
            targetSite.id!!
        ) ?: throw ResourceNotFoundException(
            "Zone",
            request.zoneId
        )

        val profile =
            visitorProfileRepository.findBySiteIdAndPhoneNumber(
                targetSite.id!!,
                request.phone
            ) ?: visitorProfileRepository.save(
                VisitorProfile(
                    name = request.name,
                    phoneNumber = request.phone,
                    site = targetSite
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

        val overdueVisit = visitRepository.findFirstByVisitorProfileIdAndVisitStatusName(profileId, OVERDUE)

        if (checkedInVisit != null) {
            throw ConflictException("Visitor already has an active visit with status CHECKED_IN")
        }

        if (overdueVisit != null) {
            throw ConflictException("Visitor already has an active visit with status OVERDUE")
        }

        val checkedInStatus =
            visitStatusRepository.findByName(CHECKED_IN)
                ?: throw ResourceNotFoundException(
                    "VisitStatus",
                    CHECKED_IN
                )

        val visit = Visit(
            visitorProfile = profile,
            site = targetSite,
            zone = zone,
            createdBy = requestedBy,
            visitStatus = checkedInStatus,
            visitorType = request.visitorType,
            purpose = request.purpose,
            checkInTime = timeUtil.timeNow()
        )

        val saved = visitRepository.save(visit)

        return visitResponseMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        visitId: UUID
    ): VisitResponse {
        val visit = findAccessibleVisit(requestedBy, visitId)

        return visitResponseMapper.toResponse(visit)
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
                VisitSpecification.search(
                    scope,
                    params
                ),
                pageable
            )
            .map(visitResponseMapper::toResponse)
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

        if (visit.visitStatus.name != CHECKED_IN && visit.visitStatus.name != OVERDUE) {
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
        visit.checkOutTime = timeUtil.timeNow()

        val checkedOut =  visitRepository.save(visit)

        return visitResponseMapper.toResponse(checkedOut)
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
