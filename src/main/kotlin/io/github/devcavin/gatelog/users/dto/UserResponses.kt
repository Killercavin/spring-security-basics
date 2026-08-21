package io.github.devcavin.gatelog.users.dto

import io.github.devcavin.gatelog.users.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val roleName: String,
    val siteId: UUID,
    val isActive: Boolean
)

fun User.toResponse() = UserResponse(
    id = id!!,
    name = name,
    email = email,
    roleName = role.name,
    siteId = site.id!!,
    isActive = isActive
)