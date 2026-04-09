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

    open fun sendVerificationCode(phoneNumber: String, verificationCode: String) {
        val message = Message(
            from = fromNumber,
            to = formatRecipient(phoneNumber),
            text = "인증번호는 $verificationCode 입니다.",
        )

        val response = messageService.send(message)
        logger.info("Solapi SMS sent: groupId={}", response.groupInfo?.groupId)
    }

    private fun formatRecipient(phoneNumber: String): String {
        val phoneDigits = phoneNumber.filter(Char::isDigit)

        if (phoneDigits.isBlank()) {
            throw IllegalArgumentException("휴대폰 번호가 필요합니다.")
        }

        return phoneDigits
    }
}
