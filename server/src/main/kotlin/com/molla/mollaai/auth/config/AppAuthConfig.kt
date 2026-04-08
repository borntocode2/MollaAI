package com.molla.mollaai.auth.config

data class AppAuthConfig(
    val googleWebClientId: String,
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
)
