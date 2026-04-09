package com.molla.mollaai

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

class BackendPhoneAuthClient(
    private val context: Context,
) {
    private val httpClient = HttpClient(OkHttp)

    suspend fun requestVerification(
        accessToken: String,
        phoneNumber: String,
    ): BackendPhoneVerificationChallenge {
        val baseUrl = context.getString(R.string.spring_server_base_url).trimEnd('/')
        require(baseUrl.isNotBlank()) { "백엔드 주소가 설정되지 않았습니다." }
        Log.i(TAG, "Requesting phone verification code: url=$baseUrl${ServerRoutes.AUTH_PHONE_REQUEST}")

        val response = httpClient.post("$baseUrl${ServerRoutes.AUTH_PHONE_REQUEST}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(
                buildJsonObject {
                    put("phoneNumber", phoneNumber)
                }.toString(),
            )
        }

        val responseText = response.bodyAsText()
        Log.i(TAG, "Phone verification request response received: status=${response.status}")
        Log.d(TAG, "Phone verification request response body: $responseText")

        if (!response.status.isSuccess()) {
            throw IllegalStateException(extractMessage(responseText) ?: "인증번호 요청에 실패했습니다.")
        }

        val jsonElement = Json.parseToJsonElement(responseText).jsonObject
        val normalizedPhoneNumber = jsonElement["phoneNumber"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("서버 응답에 phoneNumber가 없습니다.")
        val expiresAtEpochSeconds = jsonElement["expiresAtEpochSeconds"]?.jsonPrimitive?.content
            ?.toLongOrNull()
            ?: throw IllegalStateException("서버 응답에 expiresAtEpochSeconds가 없습니다.")
        val ttlSeconds = jsonElement["ttlSeconds"]?.jsonPrimitive?.content
            ?.toLongOrNull()
            ?: 180L

        return BackendPhoneVerificationChallenge(
            phoneNumber = normalizedPhoneNumber,
            expiresAtEpochSeconds = expiresAtEpochSeconds,
            ttlSeconds = ttlSeconds,
        )
    }

    suspend fun confirmVerification(
        accessToken: String,
        phoneNumber: String,
        verificationCode: String,
    ): BackendPhoneVerificationConfirmation {
        val baseUrl = context.getString(R.string.spring_server_base_url).trimEnd('/')
        require(baseUrl.isNotBlank()) { "백엔드 주소가 설정되지 않았습니다." }
        Log.i(TAG, "Confirming phone verification code: url=$baseUrl${ServerRoutes.AUTH_PHONE_VERIFY}")

        val response = httpClient.post("$baseUrl${ServerRoutes.AUTH_PHONE_VERIFY}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(
                buildJsonObject {
                    put("phoneNumber", phoneNumber)
                    put("verificationCode", verificationCode)
                }.toString(),
            )
        }

        val responseText = response.bodyAsText()
        Log.i(TAG, "Phone verification confirm response received: status=${response.status}")
        Log.d(TAG, "Phone verification confirm response body: $responseText")

        if (!response.status.isSuccess()) {
            throw IllegalStateException(extractMessage(responseText) ?: "인증번호 확인에 실패했습니다.")
        }

        val jsonElement = Json.parseToJsonElement(responseText).jsonObject
        val normalizedPhoneNumber = jsonElement["phoneNumber"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("서버 응답에 phoneNumber가 없습니다.")
        val verifiedAtEpochSeconds = jsonElement["verifiedAtEpochSeconds"]?.jsonPrimitive?.content
            ?.toLongOrNull()
            ?: throw IllegalStateException("서버 응답에 verifiedAtEpochSeconds가 없습니다.")

        return BackendPhoneVerificationConfirmation(
            phoneNumber = normalizedPhoneNumber,
            verifiedAtEpochSeconds = verifiedAtEpochSeconds,
            rawResponse = responseText,
        )
    }

    fun close() {
        Log.i(TAG, "Closing backend phone auth client")
        httpClient.close()
    }

    private companion object {
        private const val TAG = "BackendPhoneAuthClient"
    }

    private fun extractMessage(responseText: String): String? {
        return runCatching {
            val jsonObject = Json.parseToJsonElement(responseText).jsonObject
            jsonObject["message"]?.jsonPrimitive?.contentOrNull
                ?: jsonObject["detail"]?.jsonPrimitive?.contentOrNull
                ?: jsonObject["title"]?.jsonPrimitive?.contentOrNull
        }.getOrNull()
    }
}

data class BackendPhoneVerificationChallenge(
    val phoneNumber: String,
    val expiresAtEpochSeconds: Long,
    val ttlSeconds: Long,
)

data class BackendPhoneVerificationConfirmation(
    val phoneNumber: String,
    val verifiedAtEpochSeconds: Long,
    val rawResponse: String,
)
