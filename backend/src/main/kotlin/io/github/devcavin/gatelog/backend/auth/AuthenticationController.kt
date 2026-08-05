package io.github.devcavin.gatelog.backend.auth

import io.github.devcavin.gatelog.backend.auth.dto.AuthResponse
import io.github.devcavin.gatelog.backend.auth.dto.LoginRequest
import io.github.devcavin.gatelog.backend.auth.dto.RefreshTokenRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(private val authenticationService: AuthenticationService) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authenticationService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authenticationService.refresh(request.refreshToken)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<Void> {
        authenticationService.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }
}