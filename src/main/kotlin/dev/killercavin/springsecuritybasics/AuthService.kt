package dev.killercavin.springsecuritybasics

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    fun register(registerRequest: RegisterRequest): UserResponse {
        if (userRepository.existsUserByUsername(registerRequest.username)) throw IllegalArgumentException("Username already exists")

        val requestEntity = User(
            username = registerRequest.username,
            fullName = registerRequest.fullName,
            hashedPassword = passwordEncoder.encode(registerRequest.password)
        )

        val savedUser = userRepository.save(requestEntity)

        return savedUser.toUserResponse()
    }

    fun login(request: LoginRequest): UserResponse {
        val authenticationToken = UsernamePasswordAuthenticationToken(request.username, request.password)

        val userInfo = authenticationManager.authenticate(authenticationToken).principal as CustomUserDetails
        return userInfo.user.toUserResponse()
    }
}