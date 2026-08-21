package io.github.devcavin.gatelog.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Role?
}