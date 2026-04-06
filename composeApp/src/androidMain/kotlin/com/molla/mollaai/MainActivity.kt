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

    private var googleAccountLabel by mutableStateOf<String?>(null)
    private var googleSignInErrorMessage by mutableStateOf<String?>(null)
    private var isGoogleSignInInProgress by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                googleAccountLabel = googleAccountLabel,
                isGoogleSignInInProgress = isGoogleSignInInProgress,
                googleSignInErrorMessage = googleSignInErrorMessage,
                onGoogleSignIn = ::startGoogleSignIn,
                onConnectOpenAI = {
                    OpenAIConnectionLauncher.openApiKeyPage(this)
                },
            )
        }
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
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
                googleSignInErrorMessage = null
            }.onFailure { error ->
                googleSignInErrorMessage = error.message ?: "Google 로그인에 실패했습니다."
            }

            isGoogleSignInInProgress = false
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
