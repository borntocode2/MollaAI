package com.molla.mollaai.controller

/*
import com.molla.mollaai.llm.model.LlmPromptRequest
import com.molla.mollaai.llm.model.LlmPromptResponse
import com.molla.mollaai.llm.service.LlmService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/llm")
class LlmController(
    private val llmService: LlmService,
) {
    private val logger = LoggerFactory.getLogger(LlmController::class.java)

    @PostMapping("/prompt")
    fun prompt(@RequestBody request: LlmPromptRequest): LlmPromptResponse {
        val prompt = request.prompt.trim()
        if (prompt.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "prompt가 필요합니다.")
        }

        logger.info("Received LLM prompt request: model={}, promptLength={}", request.model, prompt.length)

        val response = llmService.chat(
            prompt = prompt,
            model = request.model,
        )

        return LlmPromptResponse(
            prompt = prompt,
            response = response,
        )
    }
}
*/
