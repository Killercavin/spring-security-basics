package io.github.devcavin.gatelog.backend.zones

import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.sites.SiteRepository
import io.github.devcavin.gatelog.backend.zones.dto.ZoneRequest
import io.github.devcavin.gatelog.backend.zones.dto.ZoneResponse
import io.github.devcavin.gatelog.backend.zones.dto.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ZoneService(
    private val zoneRepository: ZoneRepository,
    private val siteRepository: SiteRepository
) {
    @Transactional
    fun create(siteId: UUID, request: ZoneRequest): ZoneResponse {
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
    fun getAllBySite(siteId: UUID): List<ZoneResponse> {
        if (!siteRepository.existsById(siteId)) {
            throw ResourceNotFoundException("Site", siteId)
        }

        return zoneRepository.findAllBySiteId(siteId).map { it.toResponse() }
    }

    @Transactional
    fun update(siteId: UUID, zoneId: UUID, request: ZoneRequest): ZoneResponse {
        val zone = zoneRepository.findById(zoneId).orElseThrow { ResourceNotFoundException("Zone", zoneId) }

        if (zone.site.id != siteId) throw ResourceNotFoundException("Zone", zoneId)

        if (zone.name != request.name && zoneRepository.existsBySiteIdAndName(siteId, request.name)) {
            throw ConflictException("Zone with this name already exists under this site")
        }

        zone.name = request.name
        return zoneRepository.save(zone).toResponse()
    }

    @Transactional
    fun delete(zoneId: UUID, siteId: UUID) {
        val zone = zoneRepository.findById(zoneId)
            .orElseThrow { ResourceNotFoundException("Zone", zoneId) }

        if (zone.site.id != siteId) throw ResourceNotFoundException("Zone", zoneId)

        zoneRepository.delete(zone)
    }
}