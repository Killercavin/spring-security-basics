package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
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

        val siteId = requireNotNull(profile.site.id) {
            "Visitor profile has no site"
        }

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
                "Phone '${request.phoneNumber}' already registered at this site"
            )
        }

        profile.name = request.name
        profile.phoneNumber = request.phoneNumber

        val saved = visitorProfileRepository.save(profile)

        val visitCount =
            visitorRepository.countBySiteIdAndVisitorProfileId(
                siteId,
                profileId
            )

        return saved.toResponse(visitCount)
    }
}