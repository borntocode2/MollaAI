package com.molla.mollaai.llm.service

/*
import com.molla.mollaai.config.OpenAiProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.server.ResponseStatusException

interface LlmService {
    fun chat(prompt: String, model: String): String
}

class OpenAiChatService(
    private val openAiProperties: OpenAiProperties,
    private val restClient: RestClient,
) : LlmService {
    private val logger = LoggerFactory.getLogger(OpenAiChatService::class.java)

    override fun chat(prompt: String, model: String): String {
        val apiKey = openAiProperties.apiKey.trim()
        if (apiKey.isBlank()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI API key가 설정되지 않았습니다.")
        }

        logger.info(
            "OpenAI API key is configured: {}",
            if (apiKey.isNotBlank()) "yes (length=${apiKey.length})" else "no",
        )
        logger.info("Sending prompt to OpenAI: model={}, promptLength={}", model, prompt.length)

        val response = try {
            restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                .body(
                    OpenAiChatCompletionRequest(
                        model = model.ifBlank { "gpt-4o-mini" },
                        messages = listOf(
                            OpenAiMessage(role = "user", content = prompt),
                        ),
                    ),
                )
                .retrieve()
                .body(OpenAiChatCompletionResponse::class.java)
                ?: throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI 응답을 받지 못했습니다.")
        } catch (exception: RestClientResponseException) {
            throw ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "OpenAI 호출에 실패했습니다. (${exception.statusCode.value()})",
                exception,
            )
        } catch (exception: RestClientException) {
            throw ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "OpenAI 호출에 실패했습니다.",
                exception,
            )
        }

        return response.choices.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            .takeUnless { it.isNullOrBlank() }
            ?: throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI 응답이 비어 있습니다.")
    }
}

data class OpenAiChatCompletionRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
)

data class OpenAiMessage(
    val role: String,
    val content: String,
)

data class OpenAiChatCompletionResponse(
    val choices: List<OpenAiChoice> = emptyList(),
)

data class OpenAiChoice(
    val message: OpenAiResponseMessage,
)

data class OpenAiResponseMessage(
    val role: String,
    val content: String? = null,
)
*/
