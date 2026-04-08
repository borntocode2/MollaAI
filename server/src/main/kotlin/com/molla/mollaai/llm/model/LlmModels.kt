package com.molla.mollaai.llm.model

data class LlmPromptRequest(
    val prompt: String,
)

data class LlmPromptResponse(
    val prompt: String,
    val response: String,
)
