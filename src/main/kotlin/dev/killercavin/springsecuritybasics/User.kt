package dev.killercavin.springsecuritybasics

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @field:NotBlank(message = "Username is required") var username: String,
    @field:NotBlank(message = "Full name is required") var fullName: String,
    @field:NotBlank(message = "Password is required") var hashedPassword: String,
    @CreationTimestamp val createdAt: Instant? = null,
    @UpdateTimestamp var updatedAt: Instant? = null
)

data class RegisterRequest(
    @field:NotBlank(message = "Username can't be blank") val username: String,
    @field:NotBlank(message = "Full name can't be blank") val fullName: String,
    @field:NotBlank(message = "Password can't be blank") val password: String
)


data class LoginRequest(
    @field:NotBlank(message = "Username can't be blank") val username: String,
    @field:NotBlank(message = "Password can't be blank") val password: String
)

data class UserResponse(
    val id: Long?,
    val username: String,
    val fullName: String,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        fullName = this.fullName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
