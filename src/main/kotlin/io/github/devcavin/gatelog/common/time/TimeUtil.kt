package io.github.devcavin.gatelog.common.time

import java.time.OffsetDateTime
import java.time.ZoneOffset

object TimeUtil {

    fun timeNow(): OffsetDateTime =
        OffsetDateTime.now(ZoneOffset.UTC)

    fun startOfToday(): OffsetDateTime =
        timeNow()
            .toLocalDate()
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC)

    fun startOfTomorrow(): OffsetDateTime =
        startOfToday().plusDays(1)

    fun endOfToday(): OffsetDateTime =
        startOfTomorrow()

    fun isOvernight(
        checkInTime: OffsetDateTime,
        now: OffsetDateTime = timeNow()
    ): Boolean =
        checkInTime.toLocalDate() < now.toLocalDate()
}

