package com.molla.mollaai

/*
import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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
import kotlinx.serialization.json.put

// Spring 서버의 /api/llm/prompt 엔드포인트를 호출하는 Android 전용 클라이언트입니다.
class BackendLlmClient(
    private val context: Context,
) {
    private val httpClient = HttpClient(OkHttp)

    suspend fun prompt(prompt: String, model: String = "gpt-4o-mini"): BackendLlmResponse {
        val baseUrl = context.getString(R.string.spring_server_base_url).trimEnd('/')
        require(baseUrl.isNotBlank()) { "백엔드 주소가 설정되지 않았습니다." }
        Log.i(TAG, "Sending LLM prompt to Spring server: url=$baseUrl${ServerRoutes.LLM_PROMPT}")

        val response = httpClient.post("$baseUrl${ServerRoutes.LLM_PROMPT}") {
            contentType(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("prompt", prompt)
                    put("model", model)
                }.toString(),
            )
        }

        val responseText = response.bodyAsText()
        Log.i(TAG, "LLM response received: status=${response.status}")
        Log.d(TAG, "LLM response body: $responseText")

        if (!response.status.isSuccess()) {
            val errorMessage = extractMessage(responseText)
                ?: "서버 요청에 실패했습니다. status=${response.status.value}"
            throw IllegalStateException(errorMessage)
        }

        val jsonElement = Json.parseToJsonElement(responseText).jsonObject
        val requestPrompt = jsonElement["prompt"]?.jsonPrimitive?.content
            ?: prompt
        val responseMessage = jsonElement["response"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("서버 응답에 response가 없습니다.")

        return BackendLlmResponse(
            prompt = requestPrompt,
            response = responseMessage,
            rawResponse = responseText,
        )
    }

    fun close() {
        Log.i(TAG, "Closing backend llm client")
        httpClient.close()
    }

    private companion object {
        private const val TAG = "BackendLlmClient"
    }

    private fun extractMessage(responseText: String): String? {
        return runCatching {
            Json.parseToJsonElement(responseText).jsonObject["message"]?.jsonPrimitive?.content
        }.getOrNull()
    }
}

data class BackendLlmResponse(
    val prompt: String,
    val response: String,
    val rawResponse: String,
)
*/
