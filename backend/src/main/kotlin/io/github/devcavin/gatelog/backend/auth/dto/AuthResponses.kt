package io.github.devcavin.gatelog.backend.auth.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)