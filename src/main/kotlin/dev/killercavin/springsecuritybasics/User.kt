package dev.killercavin.springsecuritybasics

data class User(
    var name: String, var password: String, var role: Role
)