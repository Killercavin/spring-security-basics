package dev.killercavin.springsecuritybasics

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}