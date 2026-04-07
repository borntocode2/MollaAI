package com.molla.mollaai.auth.repository

import com.molla.mollaai.auth.model.AppUserRecord
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
