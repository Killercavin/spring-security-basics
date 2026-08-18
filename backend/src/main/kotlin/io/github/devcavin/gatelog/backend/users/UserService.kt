package io.github.devcavin.gatelog.backend.users

import io.github.devcavin.gatelog.backend.auth.AccessScope
import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ConflictException
import io.github.devcavin.gatelog.backend.common.exception.InvalidCredentialsException
import io.github.devcavin.gatelog.backend.common.exception.InvalidStateException
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.sites.SiteRepository
import io.github.devcavin.gatelog.backend.users.dto.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val siteRepository: SiteRepository,
    private val authorizationService: AuthorizationService,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(request: CreateUserRequest, requestedBy: User): UserResponse {
        if (userRepository.existsByEmail(request.email)) throw ConflictException("User already exists")

        val targetRole = roleRepository.findByName(request.roleName) ?: throw ResourceNotFoundException("Role", request.roleName)

        authorizationService.assertCanCreateUser(requestedBy, targetRole.name, request.siteId)

        val site = siteRepository.findById(request.siteId).orElseThrow { ResourceNotFoundException("Site", request.siteId) }

        val user = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = targetRole,
            site = site
        )

        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAll(requestedBy: User): List<UserResponse> {
        return when (val scope = authorizationService.scopeFor(requestedBy)) {
            is AccessScope.Global -> userRepository
                .findAllWithRole()
                .map { it.toResponse() }

            is AccessScope.Site -> userRepository
                .findAllBySiteIdWithRole(scope.siteId)
                .filter { it.role.name == "STAFF" }
                .map { it.toResponse() }
        }
    }

    @Transactional(readOnly = true)
    fun getById(requestedBy: User, userId: UUID): UserResponse {
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        authorizationService.assertCanViewUser(requestedBy, target)

        return target.toResponse()
    }

    @Transactional
    fun updateUser(
        requestedBy: User,
        userId: UUID,
        request: UpdateUserRequest
    ): UserResponse {
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        authorizationService.assertCanViewUser(requestedBy, target)
        authorizationService.assertCanUpdateUser(requestedBy, target, request.roleName)

        if (request.email != target.email && userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already in use: ${request.email}")
        }

        val newRole = roleRepository.findByName(request.roleName)
            ?: throw ResourceNotFoundException("Role", request.roleName)

        target.name = request.name
        target.email = request.email
        target.role = newRole

        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun deactivate(requestedBy: User, userId: UUID): UserResponse {
        if (requestedBy.id == userId) {
            throw InvalidStateException("You cannot deactivate your own account")
        }
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        authorizationService.assertCanViewUser(requestedBy, target)

        authorizationService.assertCanDeactivateUser(requestedBy, target)

        target.isActive = false
        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun activate(requestedBy: User, userId: UUID): UserResponse {
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        authorizationService.assertCanViewUser(requestedBy, target)

        target.isActive = true
        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun changePassword(
        requestedBy: User,
        request: ChangePasswordRequest
    ): UserResponse {
        if (!passwordEncoder.matches(request.currentPassword, requestedBy.passwordHash)) {
            throw InvalidCredentialsException()
        }

        requestedBy.passwordHash = passwordEncoder.encode(request.newPassword)
        return userRepository.save(requestedBy).toResponse()
    }
}