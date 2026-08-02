package io.github.devcavin.gatelog.backend.zones.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ZoneRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String
)