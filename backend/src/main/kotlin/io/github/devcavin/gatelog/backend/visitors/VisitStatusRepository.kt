package io.github.devcavin.gatelog.backend.visitors

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VisitStatusRepository : JpaRepository<VisitStatus, UUID> {
    fun findByName(name: String): VisitStatus?
}