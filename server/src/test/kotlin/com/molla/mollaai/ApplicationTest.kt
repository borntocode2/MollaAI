package com.molla.mollaai

import com.molla.mollaai.auth.AppAuthConfig
import com.molla.mollaai.auth.AppUserRecord
import com.molla.mollaai.auth.UserRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.time.Instant
import kotlin.test.*

class ApplicationTest {
    private class TestUserRepository : UserRepository {
        override fun upsert(
            googleSubject: String,
            email: String,
            displayName: String?,
            pictureUrl: String?,
            now: Instant,
        ): AppUserRecord = AppUserRecord(
            googleSubject = googleSubject,
            email = email,
            displayName = displayName,
            pictureUrl = pictureUrl,
            lastLoginAt = now,
        )

        override fun findBySubject(googleSubject: String): AppUserRecord? = null
    }

    @Test
    fun testRoot() = testApplication {
        application {
            module(
                authConfig = AppAuthConfig(
                    googleWebClientId = "test-client-id",
                    jwtSecret = "test-secret",
                    jwtIssuer = "molla-ai",
                    jwtAudience = "molla-ai-mobile",
                ),
                userRepository = TestUserRepository(),
            )
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor: ${Greeting().greet()}", response.bodyAsText())
    }
}
