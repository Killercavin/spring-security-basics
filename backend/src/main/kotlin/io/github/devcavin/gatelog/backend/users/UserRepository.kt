package io.github.devcavin.gatelog.backend.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID>{
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findAllBySiteId(siteId: UUID): List<User>
    fun findAllBySiteIdAndIsActiveTrue(siteId: UUID): List<User>

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.role WHERE u.id = :id")
    fun findByIdWithRole(id: UUID): Optional<User>

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    fun findByEmailWithRole(email: String): User?

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.role")
    fun findAllWithRole(): List<User>

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.role WHERE u.site.id = :siteId")
    fun findAllBySiteIdWithRole(siteId: UUID): List<User>

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.role JOIN FETCH u.site WHERE u.id = :id")
    fun findByIdWithRoleAndSite(id: UUID): User?
}