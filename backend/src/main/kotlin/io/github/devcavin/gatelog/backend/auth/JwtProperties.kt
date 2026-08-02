package io.github.devcavin.gatelog.backend.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gatelog.jwt")
class JwtProperties {
    var secret: String = ""
    var accessTokenExpiryMS: Long = 900_000L
    var refreshTokenExpiryDays: Long = 7L
}