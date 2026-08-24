package io.github.devcavin.gatelog.zones

import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.zones.dto.ZoneRequest
import io.github.devcavin.gatelog.zones.dto.ZoneResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/sites/{siteId}/zones")
class ZoneController(
    private val zoneService: ZoneService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun create(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable siteId: UUID,
        @Valid @RequestBody request: ZoneRequest
    ): ResponseEntity<ZoneResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(zoneService.create(requestedBy, siteId, request))

    @GetMapping("/{zoneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    fun getById(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable siteId: UUID,
        @PathVariable zoneId: UUID
    ): ResponseEntity<ZoneResponse> =
        ResponseEntity.ok(
            zoneService.getById(requestedBy, siteId, zoneId)
        )

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    fun getAllBySite(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable siteId: UUID
    ): ResponseEntity<List<ZoneResponse>> =
        ResponseEntity.ok(zoneService.getAllBySite(requestedBy, siteId))

    @PutMapping("/{zoneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun update(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable siteId: UUID,
        @PathVariable zoneId: UUID,
        @Valid @RequestBody request: ZoneRequest
    ): ResponseEntity<ZoneResponse> =
        ResponseEntity.ok(zoneService.update(requestedBy, siteId, zoneId, request))

    @DeleteMapping("/{zoneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun delete(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable siteId: UUID,
        @PathVariable zoneId: UUID
    ): ResponseEntity<Void> {
        zoneService.delete(requestedBy, siteId, zoneId)
        return ResponseEntity.noContent().build()
    }
}