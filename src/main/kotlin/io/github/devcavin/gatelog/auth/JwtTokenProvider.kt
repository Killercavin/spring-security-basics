package io.github.devcavin.gatelog.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(private val jwtProperties: JwtProperties) {

    private val signingKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: UUID, email: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiryMS)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun getUserIdFromToken(token: String): UUID {
        return UUID.fromString(getClaims(token).subject)
    }

    fun getEmailFromToken(token: String): String {
        return getClaims(token)["email"] as String
    }

    fun getRoleFromToken(token: String): String = getClaims(token)["role"] as String

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
        .payload
    }
}