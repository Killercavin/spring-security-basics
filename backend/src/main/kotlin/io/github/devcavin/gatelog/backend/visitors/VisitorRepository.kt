package io.github.devcavin.gatelog.backend.visitors

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface VisitorRepository :
    JpaRepository<Visitor, UUID>,
    JpaSpecificationExecutor<Visitor> {

    fun findTopByVisitorProfileIdAndSiteIdOrderByCheckInTimeDesc(
        visitorProfileId: UUID,
        siteId: UUID
    ): Visitor?

    fun countByVisitorProfileId(
        visitorProfileId: UUID
    ): Long
}