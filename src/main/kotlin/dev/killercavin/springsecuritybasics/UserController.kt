package dev.killercavin.springsecuritybasics

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserController {
    @GetMapping("/private")
    fun privateEndpoint(): String {
        return "This is PRIVATE – only authenticated users can see this."
    }

    @GetMapping("/public")
    fun publicEndpoint(): String {
        return "This is PUBLIC – anyone can see this."
    }
}