package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ConflictException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.dto.UpdateVisitorProfileRequest
import io.github.devcavin.gatelog.visitors.dto.VisitorProfileResponse
import io.github.devcavin.gatelog.visitors.dto.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class VisitorProfileService(
    private val visitorProfileRepository: VisitorProfileRepository,
    private val visitRepository: VisitRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        profileId: UUID
    ): VisitorProfileResponse {
        val profile = findProfileById(profileId)

        val profileSiteId = requireNotNull(profile.site.id)

        authorizationService.assertCovers(
            requestedBy,
            profileSiteId
        )

        val visitCount = getVisitCount(
            profileSiteId,
            profileId
        )

        return profile.toResponse(visitCount)
    }

    @Transactional
    fun update(
        requestedBy: User,
        profileId: UUID,
        request: UpdateVisitorProfileRequest
    ): VisitorProfileResponse {
        val profile = findProfileById(profileId)

        val profileSiteId = requireNotNull(profile.site.id)

        authorizationService.assertCovers(
            requestedBy,
            profileSiteId
        )

        if (
            request.phoneNumber != profile.phoneNumber &&
            visitorProfileRepository.existsBySiteIdAndPhoneNumber(
                profileSiteId,
                request.phoneNumber
            )
        ) {
            throw ConflictException(
                "Phone '${request.phoneNumber}' already registered at this site"
            )
        }

        profile.name = request.name
        profile.phoneNumber = request.phoneNumber

        val saved = visitorProfileRepository.save(profile)

        val visitCount = getVisitCount(
            profileSiteId,
            profileId
        )

        return saved.toResponse(visitCount)
    }

    private fun findProfileById(profileId: UUID): VisitorProfile =
        visitorProfileRepository.findById(profileId)
            .orElseThrow {
                ResourceNotFoundException(
                    "VisitorProfile",
                    profileId
                )
            }

    private fun getVisitCount(
        siteId: UUID,
        profileId: UUID
    ): Long =
        visitRepository.countBySiteIdAndVisitorProfileId(
            siteId,
            profileId
        )
}
