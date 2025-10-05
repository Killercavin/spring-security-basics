package dev.killercavin.springsecuritybasics

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val user: User) : UserDetails {
    override fun getUsername() = user.username
    override fun getPassword() = user.hashedPassword
    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_USER"))
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
