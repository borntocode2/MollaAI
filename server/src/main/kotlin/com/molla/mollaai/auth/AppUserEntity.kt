package com.molla.mollaai.auth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "app_users")
open class AppUserEntity() {
    @Id
    @Column(name = "google_subject", nullable = false, length = 255)
    open var googleSubject: String = ""

    @Column(name = "email", nullable = false, length = 320)
    open var email: String = ""

    @Column(name = "display_name")
    open var displayName: String? = null

    @Column(name = "picture_url", columnDefinition = "TEXT")
    open var pictureUrl: String? = null

    @Column(name = "last_login_at", nullable = false)
    open var lastLoginAt: Instant = Instant.EPOCH

    fun toRecord(): AppUserRecord = AppUserRecord(
        googleSubject = googleSubject,
        email = email,
        displayName = displayName,
        pictureUrl = pictureUrl,
        lastLoginAt = lastLoginAt,
    )
}
