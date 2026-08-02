package io.github.devcavin.gatelog.backend.sites.dto

import io.github.devcavin.gatelog.backend.sites.Site
import java.util.*

data class SiteResponse(
    val id: UUID,
    val name: String,
    val location: String
)

fun Site.toResponse() = SiteResponse(
    id = this.id!!,
    name = this.name,
    location = this.location
)