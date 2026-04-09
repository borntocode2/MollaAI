package com.molla.mollaai.controller

import com.molla.mollaai.phone.model.PhoneVerificationChallengeResponse
import com.molla.mollaai.phone.model.PhoneVerificationConfirmRequest
import com.molla.mollaai.phone.model.PhoneVerificationConfirmResponse
import com.molla.mollaai.phone.model.PhoneVerificationRequest
import com.molla.mollaai.phone.service.PhoneVerificationService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/phone")
class PhoneAuthController(
    private val phoneVerificationService: PhoneVerificationService,
) {
    @PostMapping("/request")
    fun request(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @RequestBody request: PhoneVerificationRequest,
    ): PhoneVerificationChallengeResponse {
        return phoneVerificationService.requestVerification(
            authorizationHeader = authorization,
            countryCode = request.countryCode,
            phoneNumber = request.phoneNumber,
        )
    }

    @PostMapping("/verify")
    fun verify(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) authorization: String?,
        @RequestBody request: PhoneVerificationConfirmRequest,
    ): PhoneVerificationConfirmResponse {
        return phoneVerificationService.confirmVerification(
            authorizationHeader = authorization,
            countryCode = request.countryCode,
            phoneNumber = request.phoneNumber,
            verificationCode = request.verificationCode,
        )
    }
}
