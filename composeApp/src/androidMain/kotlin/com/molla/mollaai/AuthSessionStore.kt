package com.molla.mollaai

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * 서버가 발급한 앱 세션 JWT를 기기 내부에 안전하게 저장합니다.
 */
class AuthSessionStore(context: Context) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun save(session: BackendSession) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_TOKEN_TYPE, session.tokenType)
            .putLong(KEY_EXPIRES_AT, session.expiresAtEpochSeconds)
            .apply()
    }

    fun load(): StoredAuthSession? {
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val tokenType = sharedPreferences.getString(KEY_TOKEN_TYPE, "Bearer") ?: "Bearer"
        val expiresAtEpochSeconds = sharedPreferences.getLong(KEY_EXPIRES_AT, 0L)

        return StoredAuthSession(
            accessToken = accessToken,
            tokenType = tokenType,
            expiresAtEpochSeconds = expiresAtEpochSeconds,
        )
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "molla_ai_auth_session"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_TOKEN_TYPE = "token_type"
        const val KEY_EXPIRES_AT = "expires_at_epoch_seconds"
    }
}

data class StoredAuthSession(
    val accessToken: String,
    val tokenType: String,
    val expiresAtEpochSeconds: Long,
)
