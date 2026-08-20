package io.github.devcavin.gatelog.backend.zones

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.sites.SiteRepository
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.zones.dto.ZoneRequest
import io.github.devcavin.gatelog.backend.zones.dto.ZoneResponse
import io.github.devcavin.gatelog.backend.zones.dto.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ZoneService(
    private val zoneRepository: ZoneRepository,
    private val siteRepository: SiteRepository,
    private val authorizationService: AuthorizationService
) {
    @Transactional
    fun create(
        requestedBy: User,
        siteId: UUID,
        request: ZoneRequest
    ): ZoneResponse {
        authorizationService.assertCovers(requestedBy, siteId)

        val site = siteRepository.findById(siteId)
            .orElseThrow { ResourceNotFoundException("Site", siteId) }

        if (zoneRepository.existsBySiteIdAndName(siteId, request.name)) {
            throw ConflictException("Zone with this name already exists under this site")
        }

        val zone = Zone(
            name = request.name,
            site = site
        )

        return zoneRepository.save(zone).toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllBySite(
        requestedBy: User,
        siteId: UUID
    ): List<ZoneResponse> {
        authorizationService.assertCovers(requestedBy, siteId)

        if (!siteRepository.existsById(siteId)) {
            throw ResourceNotFoundException("Site", siteId)
        }

        return zoneRepository.findAllBySiteId(siteId).map { it.toResponse() }
    }

    @Transactional
    fun update(
        requestedBy: User,
        siteId: UUID,
        zoneId: UUID,
        request: ZoneRequest
    ): ZoneResponse {
        authorizationService.assertCovers(requestedBy, siteId)

        val zone = zoneRepository.findById(zoneId).orElseThrow { ResourceNotFoundException("Zone", zoneId) }

        if (zone.site.id != siteId) throw ResourceNotFoundException("Zone", zoneId)

        if (zone.name != request.name && zoneRepository.existsBySiteIdAndName(siteId, request.name)) {
            throw ConflictException("Zone with this name already exists under this site")
        }

        zone.name = request.name
        return zoneRepository.save(zone).toResponse()
    }

    @Transactional
    fun delete(
        requestedBy: User,
        siteId: UUID,
        zoneId: UUID) {
        authorizationService.assertCovers(requestedBy, siteId)

        val zone = zoneRepository.findById(zoneId)
            .orElseThrow { ResourceNotFoundException("Zone", zoneId) }

        if (zone.site.id != siteId) throw ResourceNotFoundException("Zone", zoneId)

        zoneRepository.delete(zone)
    }
}