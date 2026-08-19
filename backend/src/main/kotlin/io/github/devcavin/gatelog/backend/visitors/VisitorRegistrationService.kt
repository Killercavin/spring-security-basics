package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.sites.Site
import io.github.devcavin.gatelog.backend.sites.SiteRepository
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.ReturningVisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorRegistrationRequest
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.toResponse
import io.github.devcavin.gatelog.backend.zones.Zone
import io.github.devcavin.gatelog.backend.zones.ZoneRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class VisitorRegistrationService(
    private val visitorRepository: VisitorRepository,
    private val visitorProfileRepository: VisitorProfileRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val visitorProfileService: VisitorProfileService,
    private val siteRepository: SiteRepository,
    private val zoneRepository: ZoneRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: VisitorRegistrationRequest
    ): VisitorResponse {

        val siteId = requireNotNull(requestedBy.site.id) {
            "Authenticated user has no site"
        }

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        val site = siteRepository.findById(siteId)
            .orElseThrow {
                ResourceNotFoundException("Site", siteId)
            }

        val profile = visitorProfileRepository
            .findBySiteIdAndPhoneNumber(
                siteId = siteId,
                phoneNumber = request.phone
            )
            ?: visitorProfileRepository.save(
                VisitorProfile(
                    name = request.name,
                    phoneNumber = request.phone,
                    site = site
                )
            )

        val zone = zoneRepository.findById(request.zoneId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Zone",
                    request.zoneId
                )
            }

        if (zone.site.id != siteId) {
            throw ResourceNotFoundException(
                "Zone",
                request.zoneId
            )
        }

        val visitStatus = visitStatusRepository
            .findByName("CHECKED_IN")
            ?. {
                ResourceNotFoundException(
                    "VisitStatus",
                    "CHECKED_IN"
                )
            }

        val visitor = Visitor(
            visitorProfile = profile,
            site = site,
            zone = zone,
            visitorType = visitorType,
            purpose = request.purpose,
            visitStatus = visitStatus,
            createdBy = requestedBy,
            checkInTime = OffsetDateTime.now(),
            id = TODO(),
            name = TODO(),
            phone = TODO(),
            checkOutTime = TODO()
        )

        return visitorRepository
            .save(visitor)
            .toResponse()
    }

    private fun resolveSite(requestedBy: User): Site {
        val siteId = requireNotNull(requestedBy.site.id) {
            "Authenticated user has no site"
        }

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        return siteRepository.findById(siteId)
            .orElseThrow {
                ResourceNotFoundException("Site", siteId)
            }
    }

    private fun resolveZone(
        siteId: UUID,
        zoneId: UUID?
    ): Zone? {

        if (zoneId == null) {
            return null
        }

        val zone = zoneRepository.findById(zoneId)
            .orElseThrow {
                ResourceNotFoundException("Zone", zoneId)
            }

        if (zone.site.id != siteId) {
            throw ResourceNotFoundException("Zone", zoneId)
        }

        return zone
    }

    @Transactional(readOnly = true)
    fun findReturningVisitor(
        requestedBy: User,
        phoneNumber: String
    ): ReturningVisitorResponse? {

        val siteId = requireNotNull(requestedBy.site.id)

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        val profile =
            visitorProfileRepository
                .findBySiteIdAndPhoneNumber(
                    siteId,
                    phoneNumber
                )
                ?: return null

        val lastVisit =
            visitorRepository
                .findTopByVisitorProfileIdAndSiteIdOrderByCheckInTimeDesc(
                    requireNotNull(profile.id),
                    siteId
                )

        return ReturningVisitorResponse(
            profile = profile.toSummary(),
            lastVisit = lastVisit?.toSummary()
        )
    }
}