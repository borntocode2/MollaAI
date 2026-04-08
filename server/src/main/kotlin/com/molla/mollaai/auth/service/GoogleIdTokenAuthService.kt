package com.molla.mollaai.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.molla.mollaai.auth.config.AppAuthConfig
import com.molla.mollaai.auth.model.AuthSessionResponse
import com.molla.mollaai.auth.repository.UserRepository
import java.time.Clock
import java.time.Instant
import java.util.Date
import org.slf4j.LoggerFactory

class GoogleIdTokenAuthService(
    private val config: AppAuthConfig,
    private val userRepository: UserRepository,
    private val clock: Clock = Clock.systemUTC(),
) : AuthService {
    private val logger = LoggerFactory.getLogger(GoogleIdTokenAuthService::class.java)
    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
    )
        .setAudience(listOf(config.googleWebClientId))
        .build()

    private val jwtAlgorithm = Algorithm.HMAC256(config.jwtSecret)

    override fun authenticate(idTokenString: String): AuthSessionResponse {
        logger.info("Verifying Google ID token")
        val idToken = verifier.verify(idTokenString)
            ?: throw IllegalArgumentException("Google ID 토큰 검증에 실패했습니다.")

        val payload = idToken.payload
        val googleSubject = payload.subject
        val email = payload.email ?: throw IllegalArgumentException("이메일 정보가 없습니다.")
        val displayName = payload.get("name")?.toString()
        val pictureUrl = payload.get("picture")?.toString()
        val now = Instant.now(clock)
        logger.info("Google token verified: subject={}, email={}", googleSubject, email)

        logger.info("Saving user to repository")
        val user = userRepository.upsert(
            googleSubject = googleSubject,
            email = email,
            displayName = displayName,
            pictureUrl = pictureUrl,
            now = now,
        )
        logger.info("User saved: subject={}", user.googleSubject)

        val expiresAt = now.plusSeconds(60 * 60 * 24)
        val accessToken = JWT.create()
            .withIssuer(config.jwtIssuer)
            .withAudience(config.jwtAudience)
            .withSubject(user.googleSubject)
            .withClaim("email", user.email)
            .withClaim("displayName", user.displayName)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(jwtAlgorithm)

        return AuthSessionResponse(
            accessToken = accessToken,
            expiresAtEpochSeconds = expiresAt.epochSecond,
            user = user,
        )
    }
}
