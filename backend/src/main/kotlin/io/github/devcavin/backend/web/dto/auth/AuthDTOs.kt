package io.github.devcavin.backend.web.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class LoginRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)

data class RefreshTokenRequest(
    @field:NotBlank
    val token: String
)

data class AuthenticatedUser(
    val id: UUID,
    val name: String,
    val email: String,
    val role: String,
    val siteId: UUID
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
