package io.github.devcavin.gatelog.common.time

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime

@Component
class TimeProvider(private val clock: Clock) {

    fun timeNow() : OffsetDateTime = OffsetDateTime.now(clock)
}