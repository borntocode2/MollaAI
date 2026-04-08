package com.molla.mollaai.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    var googleWebClientId: String = "",
    var jwtSecret: String = "",
    var jwtIssuer: String = "molla-ai",
    var jwtAudience: String = "molla-ai-mobile",
    var jdbcUrl: String = "",
    var username: String = "",
    var password: String = "",
)
