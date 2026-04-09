package com.molla.mollaai.phone.model

import com.molla.mollaai.auth.model.AppUserRecord

data class PhoneVerificationRequest(
    val phoneNumber: String,
)

data class PhoneVerificationConfirmRequest(
    val phoneNumber: String,
    val verificationCode: String,
)

data class PhoneVerificationChallengeResponse(
    val phoneNumber: String,
    val expiresAtEpochSeconds: Long,
    val ttlSeconds: Long,
)

data class PhoneVerificationConfirmResponse(
    val phoneNumber: String,
    val verifiedAtEpochSeconds: Long,
    val user: AppUserRecord,
)
