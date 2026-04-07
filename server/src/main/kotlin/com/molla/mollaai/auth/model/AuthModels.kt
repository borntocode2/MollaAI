package com.molla.mollaai.auth.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant

data class GoogleAuthRequest(
    val idToken: String,
)

data class AppUserRecord(
    val googleSubject: String,
    val email: String,
    val displayName: String?,
    val pictureUrl: String?,
    val lastLoginAt: Instant,
)

data class AuthSessionResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresAtEpochSeconds: Long,
    val user: AppUserRecord,
)

fun AuthSessionResponse.toJsonObject(): JsonObject = buildJsonObject {
    put("accessToken", accessToken)
    put("tokenType", tokenType)
    put("expiresAtEpochSeconds", expiresAtEpochSeconds)
    put("user", buildJsonObject {
        put("googleSubject", user.googleSubject)
        put("email", user.email)
        put("displayName", user.displayName)
        put("pictureUrl", user.pictureUrl)
        put("lastLoginAt", user.lastLoginAt.toString())
    })
}
