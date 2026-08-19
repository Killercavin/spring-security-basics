package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.sites.Site
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.UpdateVisitorProfileRequest
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorProfileResponse
import io.github.devcavin.gatelog.backend.visitors.dto.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VisitorProfileService(
    private val visitorProfileRepository: VisitorProfileRepository,
    private val visitorRepository: VisitorRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        profileId: UUID
    ): VisitorProfileResponse {

        val profile = visitorProfileRepository.findById(profileId)
            .orElseThrow {
                ResourceNotFoundException(
                    "VisitorProfile",
                    profileId
                )
            }

        authorizationService.assertCovers(
            requestedBy,
            requireNotNull(profile.site.id)
        )

        val visitCount =
            visitorRepository.countByVisitorProfileId(profileId)

        return profile.toResponse(visitCount)
    }

    @Transactional
    fun update(
        requestedBy: User,
        profileId: UUID,
        request: UpdateVisitorProfileRequest
    ): VisitorProfileResponse {

        val profile = visitorProfileRepository.findById(profileId)
            .orElseThrow {
                ResourceNotFoundException(
                    "VisitorProfile",
                    profileId
                )
            }

        val siteId = requireNotNull(profile.site.id)

        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        if (
            request.phoneNumber != profile.phoneNumber &&
            visitorProfileRepository.existsBySiteIdAndPhoneNumber(
                siteId,
                request.phoneNumber
            )
        ) {
            throw ConflictException(
                "Phone number is already registered at this site"
            )
        }

        profile.name = request.name
        profile.phoneNumber = request.phoneNumber

        return visitorProfileRepository
            .save(profile)
            .toResponse(
                visitorRepository
                    .countByVisitorProfileId(profileId)
            )
    }

    @Transactional(readOnly = true)
    fun findByPhone(
        requestedBy: User,
        phoneNumber: String
    ): VisitorProfile? {

    }

    internal fun findOrCreate(
        site: Site,
        name: String,
        phoneNumber: String
    ): VisitorProfile {
    }
}