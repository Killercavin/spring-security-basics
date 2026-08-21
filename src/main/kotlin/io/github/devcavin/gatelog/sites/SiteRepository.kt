package io.github.devcavin.gatelog.sites

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SiteRepository : JpaRepository<Site, UUID> {
    fun existsByNameAndLocation(name: String, location: String): Boolean
}