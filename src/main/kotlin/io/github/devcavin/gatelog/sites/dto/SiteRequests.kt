package io.github.devcavin.gatelog.sites.dto

import io.github.devcavin.gatelog.sites.Site
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SiteRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 255)
    val location: String
)

fun SiteRequest.toEntity(): Site {
    return Site(
        name = name,
        location = location
    )
}