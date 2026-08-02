package io.github.devcavin.gatelog.backend.zones.dto

import io.github.devcavin.gatelog.backend.zones.Zone
import java.util.*

data class ZoneResponse(
    val id: UUID,
    val name: String,
    val siteId: UUID
)

fun Zone.toResponse() = ZoneResponse(
    id = this.id!!,
    name = this.name,
    siteId = this.site.id!!
)