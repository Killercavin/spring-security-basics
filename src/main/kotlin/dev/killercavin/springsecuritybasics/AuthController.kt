package dev.killercavin.springsecuritybasics

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val response = userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}