package com.molla.mollaai.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.molla.mollaai.auth.config.AppAuthConfig

interface AccessTokenService {
    fun requireGoogleSubject(authorizationHeader: String?): String
}

class JwtAccessTokenService(
    private val config: AppAuthConfig,
) : AccessTokenService {
    private val verifier = JWT.require(Algorithm.HMAC256(config.jwtSecret))
        .withIssuer(config.jwtIssuer)
        .withAudience(config.jwtAudience)
        .build()

    override fun requireGoogleSubject(authorizationHeader: String?): String {
        val token = authorizationHeader
            ?.removePrefix("Bearer ")
            ?.trim()
            ?.takeUnless { it.isBlank() }
            ?: throw IllegalArgumentException("Authorization 헤더가 필요합니다.")

        return verifier.verify(token).subject
            ?: throw IllegalArgumentException("유효한 토큰이 아닙니다.")
    }
}
