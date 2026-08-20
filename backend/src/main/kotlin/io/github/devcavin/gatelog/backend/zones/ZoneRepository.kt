package io.github.devcavin.gatelog.backend.zones

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ZoneRepository : JpaRepository<Zone, UUID> {
    fun findAllBySiteId(siteId: UUID): List<Zone>
    fun existsBySiteIdAndName(siteId: UUID, name: String): Boolean
}