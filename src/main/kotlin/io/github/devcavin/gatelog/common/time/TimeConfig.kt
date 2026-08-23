package io.github.devcavin.gatelog.common.time

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneOffset

@Configuration
class TimeConfig {
    @Bean
    fun clock(): Clock =
        Clock.system(ZoneOffset.UTC)
}