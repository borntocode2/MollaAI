package com.molla.mollaai.controller

import com.molla.mollaai.llm.model.LlmPromptRequest
import com.molla.mollaai.llm.model.LlmPromptResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/llm")
class LlmController {
    @PostMapping("/prompt")
    fun prompt(@RequestBody request: LlmPromptRequest): LlmPromptResponse {
        val prompt = request.prompt.trim()
        if (prompt.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "prompt가 필요합니다.")
        }

        return LlmPromptResponse(
            prompt = request.prompt,
            response = "LLM endpoint is wired, but no model backend is connected yet.",
        )
    }
}
