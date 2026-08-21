package io.github.devcavin.gatelog.sites

import io.github.devcavin.gatelog.sites.dto.SiteRequest
import io.github.devcavin.gatelog.sites.dto.SiteResponse
import io.github.devcavin.gatelog.users.User
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/sites")
class SiteController(
    private val siteService: SiteService
) {

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun create(
        @Valid @RequestBody request: SiteRequest
    ): ResponseEntity<SiteResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(siteService.create(request))

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun getAll(): ResponseEntity<List<SiteResponse>> =
        ResponseEntity.ok(siteService.getAll())

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun getById(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<SiteResponse> =
        ResponseEntity.ok(siteService.getById(requestedBy, id))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: SiteRequest
    ): ResponseEntity<SiteResponse> =
        ResponseEntity.ok(siteService.update(id, request))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun delete(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        siteService.delete(id)
        return ResponseEntity.noContent().build()
    }
}