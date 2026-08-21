package io.github.devcavin.gatelog.common.exception

import java.time.OffsetDateTime

data class ErrorResponse(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val fieldErrors: Map<String, String>? = null
)
