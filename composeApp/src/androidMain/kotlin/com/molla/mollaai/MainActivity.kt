package com.molla.mollaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val googleSignInCoordinator by lazy { GoogleSignInCoordinator(this) }
    private val backendAuthClient by lazy { BackendAuthClient(this) }
    private val authSessionStore by lazy { AuthSessionStore(this) }

    private var googleAccountLabel by mutableStateOf<String?>(null)
    private var googleSignInErrorMessage by mutableStateOf<String?>(null)
    private var backendSyncMessage by mutableStateOf<String?>(null)
    private var backendSessionToken by mutableStateOf<String?>(null)
    private var isGoogleSignInInProgress by mutableStateOf(false)
    private var isBackendSyncInProgress by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        restoreStoredSession()

        setContent {
            App(
                googleAccountLabel = googleAccountLabel,
                isGoogleSignInInProgress = isGoogleSignInInProgress,
                googleSignInErrorMessage = googleSignInErrorMessage,
                backendSyncMessage = backendSyncMessage,
                isBackendSyncInProgress = isBackendSyncInProgress,
                onGoogleSignIn = ::startGoogleSignIn,
                onConnectOpenAI = {
                    OpenAIConnectionLauncher.openApiKeyPage(this)
                },
            )
        }
    }

    override fun onDestroy() {
        backendSessionToken = null
        backendAuthClient.close()
        activityScope.cancel()
        super.onDestroy()
    }

    private fun restoreStoredSession() {
        val storedSession = authSessionStore.load() ?: return
        backendSessionToken = storedSession.accessToken
        backendSyncMessage = "저장된 서버 세션을 불러왔습니다."
    }

    private fun startGoogleSignIn() {
        if (isGoogleSignInInProgress) return

        googleSignInErrorMessage = null
        isGoogleSignInInProgress = true

        activityScope.launch {
            runCatching {
                googleSignInCoordinator.signIn(this@MainActivity)
            }.onSuccess { session ->
                googleAccountLabel = buildString {
                    append(session.displayName ?: "이름 없음")
                    append(" / ")
                    append(session.email)
                }
                backendSyncMessage = "서버에 로그인 정보를 전송하는 중입니다."
                isBackendSyncInProgress = true

                runCatching {
                    backendAuthClient.exchangeGoogleIdToken(session.idToken)
                }.onSuccess { backendSession ->
                    backendSessionToken = backendSession.accessToken
                    authSessionStore.save(backendSession)
                    backendSyncMessage = "서버 로그인 완료: 앱 세션 토큰을 받았습니다."
                    googleSignInErrorMessage = null
                }.onFailure { backendError ->
                    backendSessionToken = null
                    authSessionStore.clear()
                    backendSyncMessage = null
                    googleSignInErrorMessage = backendError.message ?: "서버 인증에 실패했습니다."
                }
            }.onFailure { error ->
                googleSignInErrorMessage = error.message ?: "Google 로그인에 실패했습니다."
            }

            isBackendSyncInProgress = false
            isGoogleSignInInProgress = false
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
