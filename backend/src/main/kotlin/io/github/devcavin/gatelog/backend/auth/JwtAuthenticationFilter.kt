package io.github.devcavin.gatelog.backend.auth

import io.github.devcavin.gatelog.backend.users.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val userId = jwtTokenProvider.getUserIdFromToken(token)
            val role = jwtTokenProvider.getRoleFromToken(token)

            val user = userRepository.findByIdWithRoleAndSite(userId)

            if (user != null && user.isActive) {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val authentication = UsernamePasswordAuthenticationToken(user, null, authorities)

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization") ?: return null

        if (!bearer.startsWith("Bearer ")) return null

        return bearer.substring(7) // bearer.removePrefix("Bearer ")
    }
}