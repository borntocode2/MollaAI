package com.molla.mollaai.auth.model
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
