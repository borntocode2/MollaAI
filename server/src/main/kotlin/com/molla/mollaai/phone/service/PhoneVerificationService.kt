package com.molla.mollaai.phone.service

import com.molla.mollaai.auth.repository.UserRepository
import com.molla.mollaai.auth.service.AccessTokenService
import com.molla.mollaai.phone.model.PhoneVerificationChallengeResponse
import com.molla.mollaai.phone.model.PhoneVerificationConfirmResponse
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.time.Instant
import org.slf4j.LoggerFactory

class PhoneVerificationService(
    private val accessTokenService: AccessTokenService,
    private val userRepository: UserRepository,
    private val codeStore: PhoneVerificationCodeStore,
    private val smsSender: SolapiSmsSender,
    private val clock: Clock = Clock.systemUTC(),
) {
    private val logger = LoggerFactory.getLogger(PhoneVerificationService::class.java)
    private val secureRandom = SecureRandom()

    fun requestVerification(
        authorizationHeader: String?,
        countryCode: String,
        phoneNumber: String,
    ): PhoneVerificationChallengeResponse {
        val googleSubject = accessTokenService.requireGoogleSubject(authorizationHeader)
        val normalizedPhoneNumber = normalizePhoneNumber(countryCode, phoneNumber)
        val now = Instant.now(clock)
        val code = generateCode()
        val expiresAt = now.plus(Duration.ofMinutes(3))

        logger.info(
            "Phone verification requested: subject={}, phoneNumber={}, expiresAt={}",
            googleSubject,
            normalizedPhoneNumber,
            expiresAt,
        )

        codeStore.save(normalizedPhoneNumber, code, expiresAt)
        smsSender.sendVerificationCode(countryCode, phoneNumber, code)

        return PhoneVerificationChallengeResponse(
            phoneNumber = normalizedPhoneNumber,
            expiresAtEpochSeconds = expiresAt.epochSecond,
            ttlSeconds = Duration.between(now, expiresAt).seconds,
        )
    }

    fun confirmVerification(
        authorizationHeader: String?,
        countryCode: String,
        phoneNumber: String,
        verificationCode: String,
    ): PhoneVerificationConfirmResponse {
        val googleSubject = accessTokenService.requireGoogleSubject(authorizationHeader)
        val normalizedPhoneNumber = normalizePhoneNumber(countryCode, phoneNumber)
        val storedCode = codeStore.find(normalizedPhoneNumber)
            ?: throw IllegalArgumentException("인증번호가 만료되었거나 요청되지 않았습니다.")

        if (storedCode != verificationCode.trim()) {
            throw IllegalArgumentException("인증번호가 일치하지 않습니다.")
        }

        codeStore.delete(normalizedPhoneNumber)
        val updatedUser = userRepository.updatePhoneNumber(
            googleSubject = googleSubject,
            phoneNumber = normalizedPhoneNumber,
            now = Instant.now(clock),
        )

        return PhoneVerificationConfirmResponse(
            phoneNumber = normalizedPhoneNumber,
            verifiedAtEpochSeconds = updatedUser.phoneVerifiedAt?.epochSecond ?: Instant.now(clock).epochSecond,
            user = updatedUser,
        )
    }

    private fun generateCode(): String {
        return buildString(6) {
            repeat(6) {
                append(secureRandom.nextInt(10))
            }
        }
    }

    private fun normalizePhoneNumber(countryCode: String, phoneNumber: String): String {
        val countryDigits = countryCode.filter(Char::isDigit)
        val phoneDigits = phoneNumber.filter(Char::isDigit)

        if (countryDigits.isBlank()) {
            throw IllegalArgumentException("국가번호가 필요합니다.")
        }
        if (phoneDigits.isBlank()) {
            throw IllegalArgumentException("휴대폰 번호가 필요합니다.")
        }

        val nationalDigits = if (countryDigits == "82" && phoneDigits.startsWith("0")) {
            phoneDigits.drop(1)
        } else {
            phoneDigits
        }

        return "+$countryDigits$nationalDigits"
    }
}
