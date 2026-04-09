package com.molla.mollaai.phone.service

import com.solapi.sdk.SolapiClient
import com.solapi.sdk.message.model.Message
import org.slf4j.LoggerFactory

open class SolapiSmsSender(
    apiKey: String,
    apiSecret: String,
    private val fromNumber: String,
) {
    private val logger = LoggerFactory.getLogger(SolapiSmsSender::class.java)
    private val messageService = SolapiClient.createInstance(apiKey, apiSecret)

    open fun sendVerificationCode(countryCode: String, phoneNumber: String, verificationCode: String) {
        val message = Message(
            from = fromNumber,
            to = formatRecipient(countryCode, phoneNumber),
            text = "인증번호는 $verificationCode 입니다.",
        )

        val response = messageService.send(message)
        logger.info("Solapi SMS sent: groupId={}", response.groupInfo?.groupId)
    }

    private fun formatRecipient(countryCode: String, phoneNumber: String): String {
        val countryDigits = countryCode.filter(Char::isDigit)
        val phoneDigits = phoneNumber.filter(Char::isDigit)

        if (countryDigits.isBlank()) {
            throw IllegalArgumentException("국가번호가 필요합니다.")
        }
        if (phoneDigits.isBlank()) {
            throw IllegalArgumentException("휴대폰 번호가 필요합니다.")
        }

        return if (countryDigits == "82" && phoneDigits.startsWith("0")) {
            phoneDigits
        } else {
            phoneDigits
        }
    }
}
