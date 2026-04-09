package com.molla.mollaai

import com.molla.mollaai.auth.model.AppUserRecord
import com.molla.mollaai.auth.repository.UserRepository
import com.molla.mollaai.auth.service.AccessTokenService
import com.molla.mollaai.phone.service.InMemoryPhoneVerificationCodeStore
import com.molla.mollaai.phone.service.PhoneVerificationService
import com.molla.mollaai.phone.service.SolapiSmsSender
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class PhoneVerificationServiceTest {
    private val fixedClock = Clock.fixed(Instant.parse("2026-04-08T00:00:00Z"), ZoneOffset.UTC)

    private class RecordingSolapiSmsSender : SolapiSmsSender("dummy-key", "dummy-secret", "01000000000") {
        var lastCountryCode: String? = null
        var lastPhoneNumber: String? = null
        var lastVerificationCode: String? = null

        override fun sendVerificationCode(countryCode: String, phoneNumber: String, verificationCode: String) {
            lastCountryCode = countryCode
            lastPhoneNumber = phoneNumber
            lastVerificationCode = verificationCode
        }
    }

    private val accessTokenService = object : AccessTokenService {
        override fun requireGoogleSubject(authorizationHeader: String?): String {
            return "google-subject-123"
        }
    }

    private fun newUserRepository(): UserRepository = object : UserRepository {
        private val users = linkedMapOf<String, AppUserRecord>()

        override fun upsert(
            googleSubject: String,
            email: String,
            displayName: String?,
            pictureUrl: String?,
            now: Instant,
        ): AppUserRecord {
            val existing = users[googleSubject]
            val record = (existing ?: AppUserRecord(
                id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                googleSubject = googleSubject,
                email = email,
                displayName = displayName,
                pictureUrl = pictureUrl,
                phoneNumber = null,
                phoneVerifiedAt = null,
                lastLoginAt = now,
            )).copy(
                email = email,
                displayName = displayName,
                pictureUrl = pictureUrl,
                lastLoginAt = now,
            )
            users[googleSubject] = record
            return record
        }

        override fun findBySubject(googleSubject: String): AppUserRecord? = users[googleSubject]

        override fun updatePhoneNumber(
            googleSubject: String,
            phoneNumber: String,
            now: Instant,
        ): AppUserRecord {
            val current = users[googleSubject] ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
            val updated = current.copy(
                phoneNumber = phoneNumber,
                phoneVerifiedAt = now,
            )
            users[googleSubject] = updated
            return updated
        }

        override fun findByPhoneNumber(phoneNumber: String): AppUserRecord? {
            return users.values.firstOrNull { it.phoneNumber == phoneNumber }
        }
    }

    @Test
    fun `Korean phone number is normalized and saved after verification`() {
        val userRepository = newUserRepository()
        userRepository.upsert(
            googleSubject = "google-subject-123",
            email = "user@example.com",
            displayName = "User",
            pictureUrl = null,
            now = fixedClock.instant(),
        )
        val codeStore = InMemoryPhoneVerificationCodeStore(fixedClock)
        val smsSender = RecordingSolapiSmsSender()
        val service = PhoneVerificationService(
            accessTokenService = accessTokenService,
            userRepository = userRepository,
            codeStore = codeStore,
            smsSender = smsSender,
            clock = fixedClock,
        )

        val challenge = service.requestVerification(
            authorizationHeader = "Bearer test-token",
            countryCode = "82",
            phoneNumber = "010-1234-5678",
        )
        assertEquals("+821012345678", challenge.phoneNumber)

        val verificationCode = codeStore.find("+821012345678")
        assertNotNull(verificationCode)
        assertEquals("82", smsSender.lastCountryCode)
        assertEquals("+821012345678", smsSender.lastPhoneNumber)
        assertEquals(verificationCode, smsSender.lastVerificationCode)

        val confirmation = service.confirmVerification(
            authorizationHeader = "Bearer test-token",
            countryCode = "82",
            phoneNumber = "010-1234-5678",
            verificationCode = verificationCode!!,
        )

        assertEquals("+821012345678", confirmation.phoneNumber)
        assertEquals("+821012345678", confirmation.user.phoneNumber)
    }

    @Test
    fun `US phone number is normalized and saved after verification`() {
        val userRepository = newUserRepository()
        userRepository.upsert(
            googleSubject = "google-subject-123",
            email = "user@example.com",
            displayName = "User",
            pictureUrl = null,
            now = fixedClock.instant(),
        )
        val codeStore = InMemoryPhoneVerificationCodeStore(fixedClock)
        val smsSender = RecordingSolapiSmsSender()
        val service = PhoneVerificationService(
            accessTokenService = accessTokenService,
            userRepository = userRepository,
            codeStore = codeStore,
            smsSender = smsSender,
            clock = fixedClock,
        )

        val challenge = service.requestVerification(
            authorizationHeader = "Bearer test-token",
            countryCode = "1",
            phoneNumber = "415-555-2671",
        )
        assertEquals("+14155552671", challenge.phoneNumber)

        val verificationCode = codeStore.find("+14155552671")
        assertNotNull(verificationCode)
        assertEquals("1", smsSender.lastCountryCode)
        assertEquals("+14155552671", smsSender.lastPhoneNumber)
        assertEquals(verificationCode, smsSender.lastVerificationCode)

        val confirmation = service.confirmVerification(
            authorizationHeader = "Bearer test-token",
            countryCode = "1",
            phoneNumber = "415-555-2671",
            verificationCode = verificationCode!!,
        )

        assertEquals("+14155552671", confirmation.phoneNumber)
        assertEquals("+14155552671", confirmation.user.phoneNumber)
    }
}
