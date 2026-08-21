package io.github.devcavin.gatelog.users

import io.github.devcavin.gatelog.users.dto.ChangePasswordRequest
import io.github.devcavin.gatelog.users.dto.CreateUserRequest
import io.github.devcavin.gatelog.users.dto.UpdateUserRequest
import io.github.devcavin.gatelog.users.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun createUser(
        @AuthenticationPrincipal requestedBy: User,
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val createdUser = userService.createUser(request, requestedBy)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser)
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun getAll(
        @AuthenticationPrincipal requestedBy: User
    ): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.getAll(requestedBy))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun getById(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getById(requestedBy, id))

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun updateUser(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.updateUser(requestedBy, id, request))

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun deactivate(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.deactivate(requestedBy, id))

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun activate(
        @AuthenticationPrincipal requestedBy: User,
        @PathVariable id: UUID
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.activate(requestedBy, id))

    @PatchMapping("/me/password")
    fun changePassword(
        @AuthenticationPrincipal requestedBy: User,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.changePassword(requestedBy, request))
}