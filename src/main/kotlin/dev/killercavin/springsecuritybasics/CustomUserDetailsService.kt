package dev.killercavin.springsecuritybasics

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val repo: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = repo.getUserByUsername(username)
            ?: throw UsernameNotFoundException("User not found")
        return CustomUserDetails(user)
    }
}
