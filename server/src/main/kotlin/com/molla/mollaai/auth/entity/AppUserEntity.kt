package com.molla.mollaai.auth.entity

import com.molla.mollaai.auth.model.AppUserRecord
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "app_users")
open class AppUserEntity() {
    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    open var id: UUID = UUID.randomUUID()

    @Column(name = "google_subject", nullable = false, unique = true, length = 255)
    open var googleSubject: String = ""

    @Column(name = "email", nullable = false, length = 320)
    open var email: String = ""

    @Column(name = "display_name")
    open var displayName: String? = null

    @Column(name = "picture_url", columnDefinition = "TEXT")
    open var pictureUrl: String? = null

    @Column(name = "phone_number", unique = true, length = 32)
    open var phoneNumber: String? = null

    @Column(name = "phone_verified_at")
    open var phoneVerifiedAt: Instant? = null

    @Column(name = "last_login_at", nullable = false)
    open var lastLoginAt: Instant = Instant.EPOCH

    fun toRecord(): AppUserRecord = AppUserRecord(
        id = id,
        googleSubject = googleSubject,
        email = email,
        displayName = displayName,
        pictureUrl = pictureUrl,
        phoneNumber = phoneNumber,
        phoneVerifiedAt = phoneVerifiedAt,
        lastLoginAt = lastLoginAt,
    )
}
