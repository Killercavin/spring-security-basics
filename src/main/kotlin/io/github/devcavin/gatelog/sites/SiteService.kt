package io.github.devcavin.gatelog.sites

import io.github.devcavin.gatelog.auth.AuthorizationService
import io.github.devcavin.gatelog.common.exception.ConflictException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.sites.dto.SiteRequest
import io.github.devcavin.gatelog.sites.dto.SiteResponse
import io.github.devcavin.gatelog.sites.dto.toEntity
import io.github.devcavin.gatelog.sites.dto.toResponse
import io.github.devcavin.gatelog.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SiteService(
    private val siteRepository: SiteRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional
    fun create(request: SiteRequest): SiteResponse {
        if (
            siteRepository.existsByNameAndLocation(
                request.name,
                request.location
            )
        ) {
            throw ConflictException(
                "Site with this name and location already exists"
            )
        }

        return siteRepository
            .save(request.toEntity())
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun getAll(): List<SiteResponse> =
        siteRepository
            .findAll()
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        siteId: UUID
    ): SiteResponse {
        authorizationService.assertCovers(
            requestedBy,
            siteId
        )

        return findById(siteId)
            .toResponse()
    }

    @Transactional
    fun update(
        id: UUID,
        request: SiteRequest
    ): SiteResponse {
        val site = findById(id)

        if (
            site.name != request.name ||
            site.location != request.location
        ) {
            if (
                siteRepository.existsByNameAndLocation(
                    request.name,
                    request.location
                )
            ) {
                throw ConflictException(
                    "Site with this name and location already exists"
                )
            }
        }

        site.name = request.name
        site.location = request.location

        return siteRepository
            .save(site)
            .toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        val site = findById(id)

        siteRepository.delete(site)
    }

    /**
     * Internal site lookup.
     *
     * Keeps repository lookup and not-found handling in one place.
     * Authorization is intentionally handled by the public operation
     * that requires it.
     */
    private fun findById(id: UUID): Site =
        siteRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Site", id)
            }
}