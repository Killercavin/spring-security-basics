package dev.killercavin.springsecuritybasics

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun getUserByUsername(username: String): User?
    fun existsUserByUsername(username: String): Boolean
}