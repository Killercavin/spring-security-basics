package dev.killercavin.springsecuritybasics

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val service: AuthService) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val response = service.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(request))
    }
}