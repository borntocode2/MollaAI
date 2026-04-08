package com.molla.mollaai

import com.molla.mollaai.auth.model.AuthSessionResponse
import com.molla.mollaai.auth.service.AuthService
import com.molla.mollaai.controller.ApiExceptionHandler
import com.molla.mollaai.controller.AuthController
import com.molla.mollaai.controller.LlmController
import com.molla.mollaai.controller.RootController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ApplicationTest {
    private class TestAuthService : AuthService {
        override fun authenticate(idTokenString: String): AuthSessionResponse {
            throw IllegalArgumentException("Google ID 토큰 검증에 실패했습니다.")
        }
    }

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(
                RootController(),
                LlmController(),
                AuthController(TestAuthService()),
            )
            .setControllerAdvice(ApiExceptionHandler())
            .build()
    }

    @Test
    fun testRoot() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(content().string("Spring server is running"))
    }

    @Test
    fun testPromptEndpoint() {
        mockMvc.perform(
            post("/api/llm/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"prompt":"Say hello"}"""),
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """{"prompt":"Say hello","response":"LLM endpoint is wired, but no model backend is connected yet."}""",
                ),
            )
    }

    @Test
    fun testAuthEndpointReturnsJsonErrorForInvalidToken() {
        mockMvc.perform(
            post("/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"idToken":"invalid-token"}"""),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                content().json(
                    """{"message":"Google ID 토큰 검증에 실패했습니다."}""",
                ),
            )
    }
}
