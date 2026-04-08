package com.molla.mollaai.auth.service

import com.molla.mollaai.auth.model.AuthSessionResponse

interface AuthService {
    fun authenticate(idTokenString: String): AuthSessionResponse
}
