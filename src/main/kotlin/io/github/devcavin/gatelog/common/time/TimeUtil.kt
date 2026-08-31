package io.github.devcavin.gatelog.common.time

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.time.OffsetDateTime

@Component
class TimeUtil(private val clock: Clock) {

    fun timeNow(): OffsetDateTime =
        OffsetDateTime.now(clock)

    fun startOfToday(): OffsetDateTime =
        LocalDate.now(clock).atStartOfDay(clock.zone).toOffsetDateTime()

    fun endOfToday(): OffsetDateTime = startOfToday().plusDays(1)

    fun isOvernight(
        checkInTime: OffsetDateTime,
        now: OffsetDateTime = timeNow()
    ): Boolean =
        checkInTime.toLocalDate() < now.toLocalDate()
}

