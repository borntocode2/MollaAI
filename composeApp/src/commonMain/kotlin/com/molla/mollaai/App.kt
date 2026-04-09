package com.molla.mollaai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun App(
    googleAccountLabel: String? = null,
    isGoogleSignInInProgress: Boolean = false,
    googleSignInErrorMessage: String? = null,
    backendSyncMessage: String? = null,
    isBackendSyncInProgress: Boolean = false,
    verifiedPhoneNumber: String? = null,
    phoneCountryCode: String = "82",
    phoneNumber: String = "",
    phoneVerificationCode: String = "",
    phoneVerificationMessage: String? = null,
    phoneVerificationErrorMessage: String? = null,
    isPhoneVerificationRequestInProgress: Boolean = false,
    isPhoneVerificationConfirmInProgress: Boolean = false,
    onGoogleSignIn: () -> Unit = {},
    onPhoneCountryCodeChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onPhoneVerificationCodeChange: (String) -> Unit = {},
    onRequestPhoneVerification: () -> Unit = {},
    onConfirmPhoneVerification: () -> Unit = {},
) {
    val isLoggedIn = googleAccountLabel != null
    val isPhoneVerified = verifiedPhoneNumber != null

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "molla AI",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "먼저 Google로 로그인하세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    if (googleAccountLabel != null) {
                        Text(
                            text = "로그인됨: $googleAccountLabel",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (googleSignInErrorMessage != null) {
                        Text(
                            text = googleSignInErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (backendSyncMessage != null) {
                        Text(
                            text = backendSyncMessage,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (!isLoggedIn) {
                        Button(
                            onClick = onGoogleSignIn,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGoogleSignInInProgress,
                        ) {
                            Text(
                                text = if (isGoogleSignInInProgress) "Google 로그인 중..." else "Google 로그인",
                            )
                        }
                    } else if (isPhoneVerified) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "안녕하세요 Molla입니다.\n언제든 전화주세요",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "전화번호 인증이 완료되었습니다: $verifiedPhoneNumber",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Text(
                            text = if (isPhoneVerified) {
                                "전화번호 인증 완료: $verifiedPhoneNumber"
                            } else {
                                "전화번호 인증이 필요합니다."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )

                        OutlinedTextField(
                            value = phoneCountryCode,
                            onValueChange = onPhoneCountryCodeChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("국가 코드") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = onPhoneNumberChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("휴대폰 번호") },
                            singleLine = true,
                        )
                        Button(
                            onClick = onRequestPhoneVerification,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isPhoneVerificationRequestInProgress,
                        ) {
                            Text(
                                text = if (isPhoneVerificationRequestInProgress) {
                                    "인증번호 요청 중..."
                                } else {
                                    "인증번호 받기"
                                },
                            )
                        }
                        OutlinedTextField(
                            value = phoneVerificationCode,
                            onValueChange = onPhoneVerificationCodeChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("인증번호 6자리") },
                            singleLine = true,
                        )
                        Button(
                            onClick = onConfirmPhoneVerification,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isPhoneVerificationConfirmInProgress,
                        ) {
                            Text(
                                text = if (isPhoneVerificationConfirmInProgress) {
                                    "확인 중..."
                                } else {
                                    "인증번호 확인"
                                },
                            )
                        }
                        if (phoneVerificationMessage != null) {
                            Text(
                                text = phoneVerificationMessage,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                        if (phoneVerificationErrorMessage != null) {
                            Text(
                                text = phoneVerificationErrorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("나중에 할게요")
                    }
                }
            }
        }
    }
}
