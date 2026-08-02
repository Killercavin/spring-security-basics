package io.github.devcavin.gatelog.backend.visitors.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

data class RegisterVisitorRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = """^\+?[0-9\s\-]{7,25}$""", message = "Invalid phone number format")
    val phone: String,

    @field:NotBlank
    val visitorType: String,

    val purpose: String = "General visit",

    val zoneId: UUID
)

data class UpdateVisitorProfileRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Pattern(
        regexp = """^\+?[0-9\s\-]{7,25}$""",
        message = "Invalid phone number format"
    )
    val phoneNumber: String
)

data class VisitorSearchParams(
    val name: String? = null,
    val phone: String? = null,
    val visitorType: String? = null,
    val zoneId: UUID? = null,
    val status: String? = null,
    val from: OffsetDateTime? = null,
    val to: OffsetDateTime? = null
)