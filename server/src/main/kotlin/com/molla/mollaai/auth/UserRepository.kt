package com.molla.mollaai.auth

import java.time.Instant

interface UserRepository {
    fun upsert(
        googleSubject: String,
        email: String,
        displayName: String?,
        pictureUrl: String?,
        now: Instant,
    ): AppUserRecord

    fun findBySubject(googleSubject: String): AppUserRecord?
}
