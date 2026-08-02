package io.github.devcavin.gatelog.backend.auth

import io.github.devcavin.gatelog.backend.auth.dto.AuthResponse
import io.github.devcavin.gatelog.backend.auth.dto.LoginRequest
import io.github.devcavin.gatelog.backend.common.exception.AccountDisabledException
import io.github.devcavin.gatelog.backend.common.exception.InvalidCredentialsException
import io.github.devcavin.gatelog.backend.common.exception.InvalidRefreshTokenException
import io.github.devcavin.gatelog.backend.users.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) {
    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmailWithRole(request.email) ?: throw InvalidCredentialsException()

        if (!user.isActive) throw AccountDisabledException()

        if (!passwordEncoder.matches(request.password, user.passwordHash)) throw InvalidCredentialsException()

        val accessToken = jwtTokenProvider.generateAccessToken(
            userId = user.id!!,
            email = user.email,
            role = user.role.name
        )

        val refreshToken = issueRefreshToken(user.id!!)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    fun refresh(rawRefreshToken: String): AuthResponse {
        val storedToken = refreshTokenRepository.findByToken(rawRefreshToken) ?: throw InvalidRefreshTokenException()

        if (storedToken.expiresAt.isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            throw InvalidRefreshTokenException()
        }

        val user = storedToken.user

        if (!user.isActive) throw AccountDisabledException()

        // rotate - delete old, issue new
        refreshTokenRepository.delete(storedToken)

        val newRefreshToken = issueRefreshToken(user.id!!)

        val accessToken = jwtTokenProvider.generateAccessToken(
            userId = user.id!!,
            email = user.email,
            role = user.role.name
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun logout(rawRefreshToken: String) {
        refreshTokenRepository.findByToken(rawRefreshToken)?.let { refreshTokenRepository.delete(it) }
    }

    private fun issueRefreshToken(userId: UUID): String {
        val tokenValue = UUID.randomUUID().toString()
        val user = userRepository.getReferenceById(userId)

        val refreshToken = RefreshToken(
            token = tokenValue,
            user = user,
            expiresAt = OffsetDateTime.now().plusDays(jwtProperties.refreshTokenExpiryDays)
        )

        refreshTokenRepository.save(refreshToken)

        return tokenValue
    }
}