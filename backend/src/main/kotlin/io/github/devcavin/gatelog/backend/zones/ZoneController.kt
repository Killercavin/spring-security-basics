package io.github.devcavin.gatelog.backend.zones

import io.github.devcavin.gatelog.backend.zones.dto.ZoneRequest
import io.github.devcavin.gatelog.backend.zones.dto.ZoneResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun create(
        @PathVariable siteId: UUID,
        @Valid @RequestBody request: ZoneRequest
    ): ResponseEntity<ZoneResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(zoneService.create(siteId, request))

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER', 'STAFF')")
    fun getAllBySite(
        @PathVariable siteId: UUID
    ): ResponseEntity<List<ZoneResponse>> =
        ResponseEntity.ok(zoneService.getAllBySite(siteId))

    @PutMapping("/{zoneId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun update(
        @PathVariable siteId: UUID,
        @PathVariable zoneId: UUID,
        @Valid @RequestBody request: ZoneRequest
    ): ResponseEntity<ZoneResponse> =
        ResponseEntity.ok(zoneService.update(siteId, zoneId, request))

    @DeleteMapping("/{zoneId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun delete(
        @PathVariable siteId: UUID,
        @PathVariable zoneId: UUID
    ): ResponseEntity<Void> {
        zoneService.delete(siteId, zoneId)
        return ResponseEntity.noContent().build()
    }
}