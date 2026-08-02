package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.RegisterVisitorRequest
import io.github.devcavin.gatelog.backend.visitors.dto.ReturningVisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.UpdateVisitorProfileRequest
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorProfileResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorResponse
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorSearchParams
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/visitors")
class VisitorController(
    private val visitorService: VisitorService
) {

    @PostMapping
    fun register(
        @AuthenticationPrincipal requestedBy: User,
        @Valid @RequestBody request: RegisterVisitorRequest
    ): ResponseEntity<VisitorResponse> {
        val response = visitorService.register(requestedBy, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<VisitorResponse> {
        return ResponseEntity.ok(visitorService.getById(requestedBy, id))
    }

    @PatchMapping("/{id}/checkout")
    fun checkOut(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<VisitorResponse> {
        return ResponseEntity.ok(visitorService.checkOut(requestedBy, id))
    }

    @GetMapping
    fun search(
        @AuthenticationPrincipal requestedBy: User,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) visitorType: String?,
        @RequestParam(required = false) zoneId: UUID?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) from: OffsetDateTime?,
        @RequestParam(required = false) to: OffsetDateTime?,
        @PageableDefault(size = 20, sort = ["checkInTime"]) pageable: Pageable
    ): ResponseEntity<Page<VisitorResponse>> {
        val params = VisitorSearchParams(
            name = name,
            phone = phone,
            visitorType = visitorType,
            zoneId = zoneId,
            status = status,
            from = from,
            to = to
        )
        return ResponseEntity.ok(visitorService.search(requestedBy, params, pageable))
    }

    @GetMapping("/returning")
    fun findReturning(
        @AuthenticationPrincipal requestedBy: User,
        @RequestParam phone: String
    ): ResponseEntity<ReturningVisitorResponse> {
        val result = visitorService.findReturningVisitor(requestedBy, phone)
        return if (result != null) ResponseEntity.ok(result)
        else ResponseEntity.noContent().build()
    }

    @PutMapping("/profiles/{profileId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER', 'STAFF')")
    fun updateProfile(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable profileId: UUID,
        @Valid @RequestBody request: UpdateVisitorProfileRequest
    ): ResponseEntity<VisitorProfileResponse> =
        ResponseEntity.ok(visitorService.updateProfile(requestedBy, profileId, request))
}