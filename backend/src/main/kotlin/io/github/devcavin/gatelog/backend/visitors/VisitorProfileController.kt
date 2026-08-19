package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.UpdateVisitorProfileRequest
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorProfileResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/visitor-profiles")
class VisitorProfileController(
    private val visitorProfileService: VisitorProfileService
) {

    @GetMapping("/{profileId}")
    fun getById(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable profileId: UUID
    ): ResponseEntity<VisitorProfileResponse> =
        ResponseEntity.ok(
            visitorProfileService.getById(
                requestedBy,
                profileId
            )
        )

    @PutMapping("/{profileId}")
    fun update(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable profileId: UUID,
        @Valid @RequestBody request: UpdateVisitorProfileRequest
    ): ResponseEntity<VisitorProfileResponse> =
        ResponseEntity.ok(
            visitorProfileService.update(
                requestedBy,
                profileId,
                request
            )
        )
}