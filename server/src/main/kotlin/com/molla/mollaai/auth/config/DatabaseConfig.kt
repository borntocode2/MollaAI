package com.molla.mollaai.auth.config

import io.ktor.server.config.ApplicationConfig

data class AppDatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
)

fun loadAppDatabaseConfig(config: ApplicationConfig): AppDatabaseConfig {
    val appConfig = config.config("app")

    return AppDatabaseConfig(
        jdbcUrl = readDatabaseSetting(appConfig, "jdbcUrl", "MYSQL_JDBC_URL"),
        username = readDatabaseSetting(appConfig, "username", "MYSQL_USERNAME"),
        password = readDatabaseSetting(appConfig, "password", "MYSQL_PASSWORD"),
    )
}

private fun readDatabaseSetting(
    config: ApplicationConfig,
    key: String,
    envName: String,
): String {
    return System.getenv(envName)
        ?: config.propertyOrNull(key)?.getString()
        ?: error("$envName 환경변수가 설정되지 않았습니다.")
}
