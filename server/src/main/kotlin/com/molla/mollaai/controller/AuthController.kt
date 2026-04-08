package com.molla.mollaai.controller

import com.molla.mollaai.auth.model.AuthSessionResponse
import com.molla.mollaai.auth.model.GoogleAuthRequest
import com.molla.mollaai.auth.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/google")
    fun google(@RequestBody request: GoogleAuthRequest): AuthSessionResponse {
        return authService.authenticate(request.idToken)
    }
}
