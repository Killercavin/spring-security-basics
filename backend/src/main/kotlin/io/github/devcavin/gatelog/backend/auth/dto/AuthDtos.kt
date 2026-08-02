package io.github.devcavin.gatelog.backend.auth.dto

import java.util.UUID

data class AuthenticatedUser(
    val id: UUID,
    val name: String,
    val email: String,
    val role: String,
    val siteId: UUID
)
