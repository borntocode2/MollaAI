package com.molla.mollaai

import android.app.Activity
import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom

/**
 * Google Credential Manager를 통해 사용자 계정을 받아오는 전용 코디네이터입니다.
 * 이 단계에서는 앱 내 로그인만 처리하고, 서버 검증은 나중 단계에서 연결합니다.
 */
class GoogleSignInCoordinator(
    private val context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(activity: Activity): GoogleAccountSession {
        val webClientId = context.getString(R.string.google_web_client_id)
        require(webClientId.isNotBlank() && webClientId != "YOUR_WEB_CLIENT_ID") {
            "Google 웹 클라이언트 ID가 아직 설정되지 않았습니다."
        }

        val googleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleAccountSession(
                    displayName = googleIdTokenCredential.displayName,
                    email = googleIdTokenCredential.id,
                    profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    idToken = googleIdTokenCredential.idToken,
                )
            } catch (error: GoogleIdTokenParsingException) {
                throw IllegalStateException("Google ID 토큰을 해석하지 못했습니다.", error)
            }
        }

        throw IllegalStateException("지원하지 않는 로그인 자격 증명입니다.")
    }

    private fun createNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.URL_SAFE)
    }
}

data class GoogleAccountSession(
    val displayName: String?,
    val email: String,
    val profilePictureUrl: String?,
    val idToken: String,
)
