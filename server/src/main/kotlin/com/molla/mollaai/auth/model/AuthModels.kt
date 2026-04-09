package com.molla.mollaai.auth.model

import java.time.Instant
import java.util.UUID

data class GoogleAuthRequest(
    val idToken: String,
)

data class AppUserRecord(
    val id: UUID,
    val googleSubject: String,
    val email: String,
    val displayName: String?,
    val pictureUrl: String?,
    val phoneNumber: String?,
    val phoneVerifiedAt: Instant?,
    val lastLoginAt: Instant,
)

data class AuthSessionResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresAtEpochSeconds: Long,
    val user: AppUserRecord,
)
