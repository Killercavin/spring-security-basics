package io.github.devcavin.gatelog.common.time

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class TimeConfig(
    @Value($$"${gatelog.timezone}")
    private val timeZone: String
) {

    @Bean
    fun clock(): Clock =
        Clock.system(ZoneId.of(timeZone))
}