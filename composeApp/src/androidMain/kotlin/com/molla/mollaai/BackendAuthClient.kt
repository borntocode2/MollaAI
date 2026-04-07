package com.molla.mollaai

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Google ID Token을 우리 서버로 보내고, 앱 세션 토큰을 받아오는 클라이언트입니다.
 */
class BackendAuthClient(
    private val context: Context,
) {
    private val httpClient = HttpClient(OkHttp)

    suspend fun exchangeGoogleIdToken(idToken: String): BackendSession {
        val baseUrl = context.getString(R.string.backend_base_url).trimEnd('/')
        require(baseUrl.isNotBlank()) { "백엔드 주소가 설정되지 않았습니다." }

        val responseText = httpClient.post("$baseUrl/auth/google") {
            contentType(ContentType.Application.Json)
            header("Accept", ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("idToken", idToken)
            }.toString())
        }.bodyAsText()

        val jsonElement = Json.parseToJsonElement(responseText).jsonObject
        val accessToken = jsonElement["accessToken"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("서버 응답에 accessToken이 없습니다.")
        val tokenType = jsonElement["tokenType"]?.jsonPrimitive?.content ?: "Bearer"
        val expiresAtEpochSeconds = jsonElement["expiresAtEpochSeconds"]?.jsonPrimitive?.content
            ?.toLongOrNull()
            ?: throw IllegalStateException("서버 응답에 expiresAtEpochSeconds가 없습니다.")

        return BackendSession(
            accessToken = accessToken,
            tokenType = tokenType,
            expiresAtEpochSeconds = expiresAtEpochSeconds,
            rawResponse = responseText,
        )
    }

    fun close() {
        httpClient.close()
    }
}

data class BackendSession(
    val accessToken: String,
    val tokenType: String,
    val expiresAtEpochSeconds: Long,
    val rawResponse: String,
)
