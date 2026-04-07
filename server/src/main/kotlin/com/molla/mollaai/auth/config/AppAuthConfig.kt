package com.molla.mollaai.auth.config

import io.ktor.server.config.ApplicationConfig

data class AppAuthConfig(
    val googleWebClientId: String,
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
)

fun loadAppAuthConfig(config: ApplicationConfig): AppAuthConfig {
    val googleWebClientId = System.getenv("GOOGLE_WEB_CLIENT_ID")
        ?: error("GOOGLE_WEB_CLIENT_ID 환경변수가 설정되지 않았습니다.")
    val jwtSecret = System.getenv("APP_JWT_SECRET")
        ?: error("APP_JWT_SECRET 환경변수가 설정되지 않았습니다.")

    val appConfig = config.config("app")

    return AppAuthConfig(
        googleWebClientId = googleWebClientId,
        jwtSecret = jwtSecret,
        jwtIssuer = appConfig.propertyOrNull("jwtIssuer")?.getString() ?: "molla-ai",
        jwtAudience = appConfig.propertyOrNull("jwtAudience")?.getString() ?: "molla-ai-mobile",
    )
}
